package com.jinhx.blog.service.chat.impl;

import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.DateUtils;
import com.jinhx.blog.common.util.JsonUtils;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.chat.Message;
import com.jinhx.blog.entity.chat.User;
import com.jinhx.blog.entity.chat.vo.UserVO;
import com.jinhx.blog.service.chat.ChatService;
import com.jinhx.blog.service.chat.WebsocketServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * ChatServiceImpl
 *
 * @author jinhx
 * @since 2019-06-07
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private static final Long EXPIRES_TIME = 1000 * 60 * 60 * 24 * 30L;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private WebsocketServerEndpoint websocketServerEndpoint;

    /**
     * 新增用户
     *
     * @param id id
     * @return 用户信息
     */
    @Override
    public Response insertUser(String id){
        if (websocketServerEndpoint.isOnline(id)){
            throw new MyException(ResponseEnums.CHAT_USER_REPEAT);
        }

        User oldUserentity = getUser(RedisKeyConstants.CHAT_USER_PREFIX + id);
        if (oldUserentity != null) {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(oldUserentity, userVO);
            return Response.success(userVO);
        }

        return Response.fail(ResponseEnums.CHAT_INITT_SUCCESS);
    }

    /**
     * 用户登录
     *
     * @param user 用户对象
     * @return 用户信息
     */
    @Override
    public UserVO userLogin(User user){
        User oldUserentity = getUser(user.getId());
        if (oldUserentity != null) {
            String oldName = oldUserentity.getName();
            boolean isChangeName = false;

            if (!oldName.equals(user.getName())) {
                Set<String> names = redisTemplate.opsForSet().members(RedisKeyConstants.CHAT_NAME);
                if (!CollectionUtils.isEmpty(names)) {
                    names.forEach(namesItem -> {
                        if (namesItem.equals(user.getName())) {
                            throw new MyException(ResponseEnums.CHAT_NAME_REPEAT);
                        }
                    });
                }
                isChangeName = true;
            }

            UserVO userVO = new UserVO();
            oldUserentity.setName(user.getName());
            oldUserentity.setAvatar(user.getAvatar());
            BeanUtils.copyProperties(oldUserentity, userVO);

            redisTemplate.boundValueOps(RedisKeyConstants.CHAT_USER_PREFIX + oldUserentity.getId()).set(JsonUtils.objectToJson(oldUserentity));

            if (isChangeName){
                redisTemplate.opsForSet().add(RedisKeyConstants.CHAT_NAME, user.getName());
                redisTemplate.opsForSet().remove(RedisKeyConstants.CHAT_NAME, oldName);
            }

            return userVO;
        }

        Set<String> names = redisTemplate.opsForSet().members(RedisKeyConstants.CHAT_NAME);
        if (!CollectionUtils.isEmpty(names)){
            names.forEach(namesItem -> {
                if (namesItem.equals(user.getName())){
                    throw new MyException(ResponseEnums.CHAT_NAME_REPEAT);
                }
            });
        }

        redisTemplate.boundValueOps(RedisKeyConstants.CHAT_USER_PREFIX + user.getId()).set(JsonUtils.objectToJson(user));
        redisTemplate.opsForSet().add(RedisKeyConstants.CHAT_NAME, user.getName());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 用户登录
     *
     * @param user 用户对象
     * @return 用户信息
     */
    @Override
    public UserVO updateUser(User user){
        User oldUserentity = getUser(user.getId());
        if (oldUserentity != null) {
            String oldName = oldUserentity.getName();
            boolean isChangeName = false;

            if (!oldName.equals(user.getName())){
                Set<String> names = redisTemplate.opsForSet().members(RedisKeyConstants.CHAT_NAME);
                if (!CollectionUtils.isEmpty(names)){
                    names.forEach(namesItem -> {
                        if (namesItem.equals(user.getName())){
                            throw new MyException(ResponseEnums.CHAT_NAME_REPEAT);
                        }
                    });
                }
                isChangeName = true;
            }

            UserVO userVO = new UserVO();
            oldUserentity.setName(user.getName());
            oldUserentity.setAvatar(user.getAvatar());
            BeanUtils.copyProperties(oldUserentity, userVO);

            redisTemplate.boundValueOps(RedisKeyConstants.CHAT_USER_PREFIX + oldUserentity.getId()).set(JsonUtils.objectToJson(oldUserentity));
            if (isChangeName){
                redisTemplate.opsForSet().add(RedisKeyConstants.CHAT_NAME, user.getName());
                redisTemplate.opsForSet().remove(RedisKeyConstants.CHAT_NAME, oldName);
            }

            return userVO;
        }

        throw new MyException(ResponseEnums.CHAT_USER_NOT_EXIST);
    }

    /**
     * 获取当前窗口用户信息
     *
     * @param id id
     * @return 当前窗口用户信息
     */
    @Override
    public User getUser(String id) {
        if (id != null) {
            String value = null;
            if (id.startsWith(RedisKeyConstants.CHAT_USER_PREFIX)) {
                value = redisTemplate.boundValueOps(id).get();
            } else {
                value = redisTemplate.boundValueOps(RedisKeyConstants.CHAT_USER_PREFIX + id).get();
            }
            if (value != null) {
                return JsonUtils.jsonToObject(value, User.class);
            }
        }
        return null;
    }

    /**
     * 推送消息，储存到Redis数据库中
     *
     * @param fromId  推送方ID
     * @param toId    接收方ID
     * @param message 消息
     */
    @Override
    public void pushMessage(String fromId, String toId, String message) {
        Message entity = new Message();
        entity.setMessage(message);
        entity.setFrom(getUser(fromId));
        entity.setCreateTime(DateUtils.getNowTimeString());
        if (toId != null) {
            //查询接收方信息
            entity.setTo(getUser(toId));
            //单个用户推送
            push(entity, RedisKeyConstants.CHAT_FROM_PREFIX + fromId + RedisKeyConstants.CHAT_TO_PREFIX + toId);
        } else {
            //公共消息 -- 群组
            entity.setTo(null);
            push(entity, RedisKeyConstants.CHAT_COMMON_PREFIX + fromId);
        }
    }

    /**
     * 推送消息
     *
     * @param message 消息对象
     * @param key    key
     */
    private void push(Message message, String key) {
        //这里按照 PREFIX_ID 格式，作为KEY储存消息记录
        //但一个用户可能推送很多消息，VALUE应该是数组
        List<Message> list = new ArrayList<>();
        String value = redisTemplate.boundValueOps(key).get();
        if (value == null) {
            //第一次推送消息
            list.add(message);
        } else {
            //第n次推送消息
            list = Objects.requireNonNull(JsonUtils.jsonToList(value, Message.class));
            list.add(message);
        }
        redisTemplate.boundValueOps(key).set(JsonUtils.objectToJson(list));
    }

    /**
     * 获取在线用户列表
     *
     * @return 在线用户列表
     */
    @Override
    public List<UserVO> listOnlineUsers() {
        List<UserVO> list = new ArrayList<>();
        Set<String> keys = redisTemplate.keys(RedisKeyConstants.CHAT_USER_PREFIX + RedisKeyConstants.REDIS_MATCH_PREFIX);
        if (keys != null && keys.size() > 0) {
            keys.forEach(key -> {
                if (websocketServerEndpoint.isOnline(key.substring(key.lastIndexOf(":") + 1))){
                    UserVO userVO = new UserVO();
                    BeanUtils.copyProperties(getUser(key), userVO);
                    list.add(userVO);
                }
            });
        }
        return list;
    }

    /**
     * 获取公共聊天消息列表
     *
     * @return 消息列表
     */
    @Override
    public List<Message> listCommonMessages() {
        List<Message> list = new ArrayList<>();
        Set<String> keys = redisTemplate.keys(RedisKeyConstants.CHAT_COMMON_PREFIX + RedisKeyConstants.REDIS_MATCH_PREFIX);
        if (keys != null && keys.size() > 0) {
            keys.forEach(key -> {
                String value = redisTemplate.boundValueOps(key).get();
                List<Message> messageList = Objects.requireNonNull(JsonUtils.jsonToList(value, Message.class));
                list.addAll(messageList);
            });
        }
        sort(list);
        return list;
    }

    /**
     * 获取指定用户的聊天消息列表
     *
     * @param fromId 推送方ID
     * @param toId   接收方ID
     * @return 消息列表
     */
    @Override
    public List<Message> listMessages(String fromId, String toId) {
        List<Message> list = new ArrayList<>();
        //A -> B
        String fromTo = redisTemplate.boundValueOps(RedisKeyConstants.CHAT_FROM_PREFIX + fromId + RedisKeyConstants.CHAT_TO_PREFIX + toId).get();
        //B -> A
        String toFrom = redisTemplate.boundValueOps(RedisKeyConstants.CHAT_FROM_PREFIX + toId + RedisKeyConstants.CHAT_TO_PREFIX + fromId).get();

        List<Message> fromToList = JsonUtils.jsonToList(fromTo, Message.class);
        List<Message> toFromList = JsonUtils.jsonToList(toFrom, Message.class);
        if (!CollectionUtils.isEmpty(fromToList)) {
            JsonUtils.jsonToList(fromTo, Message.class);
            list.addAll(fromToList);
        }
        if (!CollectionUtils.isEmpty(toFromList)) {
            list.addAll(toFromList);
        }

        if (list.size() > 0) {
            sort(list);
            return list;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 删除指定ID在Redis中储存的数据
     *
     * @param id id
     */
    @Override
    public void delete(String id) {
        if (id != null) {
            log.info("从Redis中删除此Key: " + id);
            redisTemplate.delete(RedisKeyConstants.CHAT_USER_PREFIX + id);
        }
    }

    /**
     * 清除注册时间超过30天的账户
     */
    @Override
    public void clearUser() {
        log.info("清除注册时间超过30天的账户，以及其会话信息");
        List<UserVO> userVOList = listOnlineUsers();
        userVOList.forEach(user -> {
            if ((DateUtils.getNowTimeLong() - DateUtils.convertTimeToLong(user.getCreateTime())) >= EXPIRES_TIME) {
                delete(user.getId());
                if (redisTemplate.boundValueOps(RedisKeyConstants.CHAT_COMMON_PREFIX + user.getId()).get() != null) {
                    redisTemplate.delete(RedisKeyConstants.CHAT_COMMON_PREFIX + user.getId());
                }
                if (redisTemplate.boundValueOps(RedisKeyConstants.CHAT_FROM_PREFIX + user.getId()).get() != null) {
                    redisTemplate.delete(RedisKeyConstants.CHAT_FROM_PREFIX + user.getId());
                }
            }
        });
    }

    /**
     * 对List集合中的数据按照时间顺序排序
     *
     * @param list List<Message>
     */
    private void sort(List<Message> list) {
        list.sort(Comparator.comparing(Message::getCreateTime));
    }

}
