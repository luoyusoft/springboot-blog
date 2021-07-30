package com.jinhx.blog.service.chat;

import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.DateUtils;
import com.jinhx.blog.common.util.JsonUtils;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.chat.Message;
import com.jinhx.blog.entity.chat.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebsocketServerEndpoint
 *
 * @author jinhx
 * @since 2019-06-07
 */
@Slf4j
@Component
@ServerEndpoint(value = "/chat/ws/{id}")
public class WebsocketServerEndpoint {

    private static ChatService chatService;

    @Autowired
    public void setChatService(ChatService chatService) {
        WebsocketServerEndpoint.chatService = chatService;
    }

    //在线连接数
    private static long online = 0;

    //用于存放当前Websocket对象的Set集合
    private static CopyOnWriteArraySet<WebsocketServerEndpoint> websocketServerEndpoints = new CopyOnWriteArraySet<>();

    //与客户端的会话Session
    private Session session;

    //当前会话窗口ID
    private String fromId = "";

    /**
     * 链接成功调用的方法
     *
     * @param session session
     * @param id id
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        log.info("Websocket链接成功：{}", id);
        this.session = session;

        //将当前websocket对象存入到Set集合中
        websocketServerEndpoints.add(this);

        //在线人数+1
        addOnlineCount();

        log.info("Websocket有新窗口开始监听：" + id + "，当前在线人数为：" + getOnlineCount());

        fromId = id;

        try {
            User user = chatService.getUser(fromId);
            //群发消息
            Map<String, Object> map = new HashMap<>();
            map.put("msg", "用户\"" + user.getName() + "\"已上线");
            sendMore(JsonUtils.objectToJson(map));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        log.info("Websocket链接成功关闭");

        // 移除当前Websocket对象
        websocketServerEndpoints.remove(this);

        // 在线人数-1
        subOnLineCount();

        log.info("Websocket链接关闭，当前在线人数：" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message message
     */
    @OnMessage
    public void onMessage(String message) throws IOException {
        log.info("Websocket接收到窗口：" + fromId + "的信息：" + message);

        chatService.pushMessage(fromId, null, message);

        //群发消息
        sendMore(getData(null, message));
    }

    /**
     * 链接出错时调用
     *
     * @param e e
     */
    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    /**
     * 推送消息
     *
     * @param message 详细对象
     */
    private void sendMessage(String message) throws Exception {
        session.getBasicRemote().sendText(message);
    }

    /**
     * 封装返回消息
     *
     * @param toId    指定窗口ID
     * @param message 消息内容
     * @return 返回消息
     */
    private String getData(String toId, String message) {
        Message entity = new Message();
        entity.setMessage(message);
        entity.setCreateTime(DateUtils.getNowTimeString());
        entity.setFrom(chatService.getUser(fromId));
        entity.setTo(chatService.getUser(toId));
        return JsonUtils.objectToJson(Response.success(entity));
    }

    /**
     * 群发消息
     *
     * @param data data
     */
    private void sendMore(String data) {
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            try {
                websocketServerEndpoint.sendMessage(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 向指定窗口推送消息
     *
     * @param toId    接收方ID
     * @param message 消息对象
     */
    public void sendTo(String toId, Message message) {
        fromId = message.getFrom().getId();
        if (websocketServerEndpoints.size() <= 1) {
            throw new MyException(ResponseEnums.CHAT_USER_OFF_LINE);
        }
        boolean flag = false;
        for (WebsocketServerEndpoint endpoint : websocketServerEndpoints) {
            try {
                if (endpoint.fromId.equals(toId)) {
                    flag = true;
                    log.info("Websocket：" + message.getFrom().getId() + "推送消息到窗口：" + toId + " ，推送内容：" + message.getMessage());

                    endpoint.sendMessage(getData(toId, message.getMessage()));
                    chatService.pushMessage(fromId, toId, message.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new MyException(ResponseEnums.CHAT_SEND_ERROR);
            }
        }
        if (!flag) {
            throw new MyException(ResponseEnums.CHAT_USER_OFF_LINE);
        }
    }

    /**
     * 是否在线
     *
     * @param id id
     * @return 是否在线
     */
    public Boolean isOnline(String id) {
        if (websocketServerEndpoints.size() < 1) {
            return false;
        }
        for (WebsocketServerEndpoint endpoint : websocketServerEndpoints) {
            if (endpoint.fromId.equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 在线人数-1
     */
    private void subOnLineCount() {
        WebsocketServerEndpoint.online--;
    }

    /**
     * 获取在线人数
     *
     * @return 在线人数
     */
    private synchronized long getOnlineCount() {
        return online;
    }

    /**
     * 在线人数+1
     */
    private void addOnlineCount() {
        WebsocketServerEndpoint.online++;
    }

}
