package com.jinhx.blog.service.chat;

import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.chat.Message;
import com.jinhx.blog.entity.chat.User;
import com.jinhx.blog.entity.chat.vo.UserVO;

import java.util.List;

/**
 * ChatService
 *
 * @author jinhx
 * @since 2019-06-07
 */
public interface ChatService {

    /**
     * 新增用户
     *
     * @param id id
     * @return 用户信息
     */
    Response<UserVO> insertUser(String id);

    /**
     * 用户登录
     *
     * @param user 用户对象
     * @return 用户信息
     */
    UserVO userLogin(User user);

    /**
     * 用户登录
     *
     * @param user 用户对象
     * @return 用户信息
     */
    UserVO updateUser(User user);

    /**
     * 获取当前窗口用户信息
     *
     * @param id id
     * @return 当前窗口用户信息
     */
    User getUser(String id);

    /**
     * 推送消息，储存到Redis数据库中
     *
     * @param fromId  推送方ID
     * @param toId    接收方ID
     * @param message 消息
     */
    void pushMessage(String fromId, String toId, String message);

    /**
     * 获取在线用户列表
     *
     * @return 在线用户列表
     */
    List<UserVO> listOnlineUsers();

    /**
     * 获取公共聊天消息列表
     *
     * @return 消息列表
     */
    List<Message> listCommonMessages();

    /**
     * 获取指定用户的聊天消息列表
     *
     * @param fromId 推送方ID
     * @param toId   接收方ID
     * @return 消息列表
     */
    List<Message> listMessages(String fromId, String toId);

    /**
     * 删除指定ID在Redis中储存的数据
     *
     * @param id id
     */
    void delete(String id);

    /**
     * 清除注册时间超过30天的账户
     */
    void clearUser();

}
