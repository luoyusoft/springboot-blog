package com.jinhx.blog.common.util;

import org.springframework.data.redis.core.*;

import java.util.concurrent.TimeUnit;

/**
 * RedisUtils
 *
 * @author jinhx
 * @since 2018-10-06
 */
public class RedisUtils {

    private static final RedisOperations<String, Object> redisOperations;

    private static final ValueOperations<String, String> valueOperations;

    private static final HashOperations<String, String, Object> hashOperations;

    private static final ListOperations<String, Object> listOperations;

    private static final SetOperations<String, Object> setOperations;

    private static final ZSetOperations<String, Object> zSetOperations;

    static {
        redisOperations = SpringUtils.getBean("redisTemplate", RedisOperations.class);
        valueOperations = SpringUtils.getBean(ValueOperations.class);
        hashOperations = SpringUtils.getBean(HashOperations.class);
        listOperations = SpringUtils.getBean(ListOperations.class);
        setOperations = SpringUtils.getBean(SetOperations.class);
        zSetOperations = SpringUtils.getBean(ZSetOperations.class);
    }

    /**
     * 默认过期时长，单位：毫秒
     */
    public final static long DEFAULT_EXPIRE = 60 * 60 * 24 * 1000;

    /**
     * 不设置过期时长
     */
    public final static long NOT_EXPIRE = -1;

    /**
     * 设置值与过期时间
     *
     * @param key key
     * @param value value
     * @param expire expire
     */
    public static void set(String key, Object value, long expire) {
        valueOperations.set(key, JsonUtils.objectToJson(value));
        if(expire != NOT_EXPIRE){
            redisOperations.expire(key, expire, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 设置值，默认过期时间1天
     *
     * @param key key
     * @param value value
     */
    public static void set(String key, Object value){
        set(key, value, DEFAULT_EXPIRE);
    }

    /**
     * 获取对象，同时设置过期时间
     *
     * @param key key
     * @param clazz clazz
     * @param expire expire
     * @param <T> <T>
     * @return 对象
     */
    public static <T> T getObj(String key, Class<T> clazz, long expire) {
        String value = valueOperations.get(key);
        if(expire != NOT_EXPIRE){
            redisOperations.expire(key, expire, TimeUnit.MILLISECONDS);
        }
        return value == null ? null : JsonUtils.jsonToObject(value, clazz);
    }

    /**
     * 获取对象，不设置过期时间
     *
     * @param key key
     * @param clazz clazz
     * @param <T> <T>
     * @return 对象
     */
    public static <T> T getObj(String key, Class<T> clazz) {
        return getObj(key, clazz, NOT_EXPIRE);
    }

    /**
     * 获取值，同时设置过期时间
     *
     * @param key key
     * @param expire expire
     * @return 值
     */
    public static String get(String key, long expire) {
        String value = valueOperations.get(key);
        if(expire != NOT_EXPIRE){
            redisOperations.expire(key, expire, TimeUnit.MILLISECONDS);
        }
        return value;
    }

    /**
     * 获取值，不设置过期时间
     *
     * @param key key
     * @return 值
     */
    public static String get(String key) {
        return get(key, NOT_EXPIRE);
    }

    /**
     * 删除
     *
     * @param key key
     */
    public static void delete(String key) {
        redisOperations.delete(key);
    }

    /**
     * 更新过期时间
     *
     * @param key key
     */
    public static void updateExpire(String key) {
        redisOperations.expire(key, DEFAULT_EXPIRE, TimeUnit.MILLISECONDS);
    }

    /**
     * 如果key不存在就设置value值，并返回true
     * 如果key存在则不进行操作，并返回false
     *
     * @param key key
     * @param value value
     * @param expire expire
     * @return 是否成功
     */
    public static Boolean setIfAbsent(String key, String value, long expire) {
        return valueOperations.setIfAbsent(key, value, expire, TimeUnit.MILLISECONDS);
    }

    /**
     * 如果key不存在就初始化为0，然后加1，再返回
     * 如果key存在则直接加1，再返回
     *
     * @param key key
     * @return 是否成功
     */
    public static Long increment(String key) {
        return valueOperations.increment(key);
    }

}
