package com.jinhx.blog.controller.chat;

import com.jinhx.blog.common.aop.annotation.LogView;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.DateUtils;
import com.jinhx.blog.common.util.EncodeUtils;
import com.jinhx.blog.common.util.IPUtils;
import com.jinhx.blog.common.util.UserAgentUtils;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.chat.Message;
import com.jinhx.blog.entity.chat.User;
import com.jinhx.blog.entity.chat.vo.UserVO;
import com.jinhx.blog.service.chat.ChatService;
import com.jinhx.blog.service.chat.WebsocketServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * ChatController
 *
 * @author jinhx
 * @since 2019-06-07
 */
@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private WebsocketServerEndpoint websocketServerEndpoint;

    /********************** portal ********************************/

    /**
     * 新增用户
     * @param request request
     * @return 用户信息
     */
    @PostMapping("/user")
    public Response insertUser(HttpServletRequest request) throws Exception {
        String ip = IPUtils.getIpAddr(request);
        String browserName = UserAgentUtils.getBrowserName(request);
        String browserVersion = UserAgentUtils.getBrowserVersion(request);
        String deviceManufacturer = UserAgentUtils.getDeviceManufacturer(request);
        String deviceType = UserAgentUtils.getDeviceType(request);
        String osVersion = UserAgentUtils.getOsVersion(request);

        String id = EncodeUtils.encoderByMD5(ip + browserName + browserVersion + deviceManufacturer + deviceType + osVersion);

        return chatService.insertUser(id);
    }

    /**
     * 用户登录
     * @param request request
     * @param user 用户对象
     * @return 用户信息
     */
    @PostMapping("/user/login")
    @LogView(module = 2)
    public Response userLogin(HttpServletRequest request, @RequestBody User user) throws Exception {
        if (user == null || StringUtils.isEmpty(user.getName()) || StringUtils.isEmpty(user.getAvatar())){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "名称，头像不能为空");
        }

        String ip = IPUtils.getIpAddr(request);
        String borderName = UserAgentUtils.getBrowserName(request);
        String browserVersion = UserAgentUtils.getBrowserVersion(request);
        String deviceManufacturer = UserAgentUtils.getDeviceManufacturer(request);
        String devicetype = UserAgentUtils.getDeviceType(request);
        String osVersion = UserAgentUtils.getOsVersion(request);

        String id = EncodeUtils.encoderByMD5(ip + borderName + browserVersion + deviceManufacturer + devicetype + osVersion).replaceAll("/", "");

        if (websocketServerEndpoint.isOnline(id)){
            throw new MyException(ResponseEnums.CHAT_USER_REPEAT);
        }

        user.setId(id);
        user.setIp(ip);
        user.setCreateTime(DateUtils.getNowTimeString());
        user.setBorderName(borderName);
        user.setBorderVersion(browserVersion);
        user.setDeviceManufacturer(deviceManufacturer);
        user.setDeviceType(devicetype);
        user.setOsVersion(osVersion);

        return Response.success(chatService.userLogin(user));
    }

    /**
     * 用户登录
     * @param request request
     * @param user 用户对象
     * @return 用户信息
     */
    @PutMapping("/user")
    public Response updateUser(HttpServletRequest request, @RequestBody User user) throws Exception {
        if (user == null || StringUtils.isEmpty(user.getId())
                || (StringUtils.isEmpty(user.getName()) && StringUtils.isEmpty(user.getAvatar()))){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "id，名称，头像不能为空");
        }

        String ip = IPUtils.getIpAddr(request);
        String borderName = UserAgentUtils.getBrowserName(request);
        String browserVersion = UserAgentUtils.getBrowserVersion(request);
        String deviceManufacturer = UserAgentUtils.getDeviceManufacturer(request);
        String devicetype = UserAgentUtils.getDeviceType(request);
        String osVersion = UserAgentUtils.getOsVersion(request);

        String id = EncodeUtils.encoderByMD5(ip + borderName + browserVersion + deviceManufacturer + devicetype + osVersion);

        if (!id.equals(user.getId())){
            throw new MyException(ResponseEnums.CHAT_NO_AUTH);
        }

        if (!websocketServerEndpoint.isOnline(id)){
            throw new MyException(ResponseEnums.CHAT_USER_OFF_LINE);
        }

        user.setId(id);
        user.setIp(ip);
        user.setCreateTime(DateUtils.getNowTimeString());
        user.setBorderName(borderName);
        user.setBorderVersion(browserVersion);
        user.setDeviceManufacturer(deviceManufacturer);
        user.setDeviceType(devicetype);
        user.setOsVersion(osVersion);

        return Response.success(chatService.updateUser(user));
    }

    /**
     * 获取当前窗口用户信息
     * @param id id
     * @return 当前窗口用户信息
     */
    @GetMapping("/user/{id}")
    public Response getUser(@PathVariable("id") String id) {
        User user = chatService.getUser(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return Response.success(userVO);
    }

    /**
     * 向指定窗口推送消息
     * @param toId    接收方ID
     * @param message 消息对象
     */
    @PostMapping("/message/{toId}")
    public Response insertMessage(@PathVariable("toId") String toId, @RequestBody Message message) {
        WebsocketServerEndpoint endpoint = new WebsocketServerEndpoint();
        endpoint.sendTo(toId, message);
        return Response.success();
    }

    /**
     * 获取在线用户列表
     * @return 在线用户列表
     */
    @GetMapping("/listonlineusers")
    public Response listOnlineUsers() {
        return Response.success(chatService.listOnlineUsers());
    }

    /**
     * 获取公共聊天消息列表
     * @return 消息列表
     */
    @GetMapping("/listcommonmessages")
    public Response listCommonMessages() {
        return Response.success(chatService.listCommonMessages());
    }

    /**
     * 获取指定用户的聊天消息列表
     * @param fromId 推送方ID
     * @param toId   接收方ID
     * @return 消息列表
     */
    @GetMapping("/listmessages/{fromId}/{toId}")
    public Response listMessages(@PathVariable("fromId") String fromId, @PathVariable("toId") String toId) {
        List<Message> list = chatService.listMessages(fromId, toId);
        return Response.success(list);
    }

}
