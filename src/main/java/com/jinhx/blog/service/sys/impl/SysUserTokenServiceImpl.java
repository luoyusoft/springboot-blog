package com.jinhx.blog.service.sys.impl;

import com.jinhx.blog.common.auth.TokenGenerator;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.common.util.RedisUtils;
import com.jinhx.blog.entity.sys.SysUserToken;
import com.jinhx.blog.service.sys.SysUserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * SysUserTokenServiceImpl
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public class SysUserTokenServiceImpl implements SysUserTokenService {

    /**
     * 12小时后过期
     */
    private final static int EXPIRE = 12 * 60 * 60 * 1000;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 生成token
     *
     * @param userId 用户id
     * @return token
     */
    @Override
    public String createToken(Integer userId) {
        // 生成一个token
        String token = TokenGenerator.generateValue();

        String tokenKey= RedisKeyConstants.MANAGE_SYS_USER_TOKEN+token;
        String userIdKey= RedisKeyConstants.MANAGE_SYS_USER_TOKEN+userId;

        // 判断是否生成过token
        String tokenInRedis = redisUtils.get(userIdKey);
        if(!StringUtils.isEmpty(tokenInRedis)){
            // 将原来的token删除
            redisUtils.delete(RedisKeyConstants.MANAGE_SYS_USER_TOKEN+tokenInRedis);
        }
        // 将token存进redis
        redisUtils.set(tokenKey, userId, EXPIRE);
        redisUtils.set(userIdKey, token, EXPIRE);

        return token;
    }

    /**
     * 根据token查询token用户信息
     *
     * @param token token
     * @return token用户信息
     */
    @Override
    public SysUserToken getSysUserTokenByToken(String token) {
        String userId=redisUtils.get(token);
        if(StringUtils.isEmpty(userId)){
            return null;
        }
        SysUserToken sysUserToken=new SysUserToken();
        sysUserToken.setToken(token);
        sysUserToken.setUserId(Integer.parseInt(userId));
        return sysUserToken;
    }

    /**
     * 退出登录
     *
     * @param userId 用户id
     */
    @Override
    public void logout(Integer userId) {
        String userIdKey = RedisKeyConstants.MANAGE_SYS_USER_TOKEN + userId;
        String token = redisUtils.get(userIdKey);
        String tokenKey = RedisKeyConstants.MANAGE_SYS_USER_TOKEN + token;
        redisUtils.delete(userIdKey);
        redisUtils.delete(tokenKey);
    }

    /**
     * 续期token
     *
     * @param userId 用户id
     * @param accessToken 新token
     */
    @Override
    public void refreshToken(Integer userId, String accessToken) {
        String tokenKey = RedisKeyConstants.MANAGE_SYS_USER_TOKEN + accessToken;
        String userIdKey = RedisKeyConstants.MANAGE_SYS_USER_TOKEN + userId;
        redisUtils.updateExpire(tokenKey);
        redisUtils.updateExpire(userIdKey);
    }

}
