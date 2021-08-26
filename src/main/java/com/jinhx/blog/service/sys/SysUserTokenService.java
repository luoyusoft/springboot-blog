package com.jinhx.blog.service.sys;

import com.jinhx.blog.common.auth.TokenGenerator;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.common.util.RedisUtils;
import com.jinhx.blog.entity.sys.SysUserToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * SysUserTokenService
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public class SysUserTokenService {

    /**
     * 12小时后过期
     */
    private final static int EXPIRE = 12 * 60 * 60 * 1000;

    /**
     * 生成token
     *
     * @param userId 用户id
     * @return token
     */
    public String createToken(Long userId) {
        // 生成一个token
        String token = TokenGenerator.generateValue();

        String tokenKey= RedisKeyConstants.MANAGE_SYS_USER_TOKEN+token;
        String userIdKey= RedisKeyConstants.MANAGE_SYS_USER_TOKEN+userId;

        // 判断是否生成过token
        String tokenInRedis = RedisUtils.get(userIdKey);
        if(!StringUtils.isEmpty(tokenInRedis)){
            // 将原来的token删除
            RedisUtils.delete(RedisKeyConstants.MANAGE_SYS_USER_TOKEN+tokenInRedis);
        }
        // 将token存进redis
        RedisUtils.set(tokenKey, userId, EXPIRE);
        RedisUtils.set(userIdKey, token, EXPIRE);

        return token;
    }

    /**
     * 根据token查询token用户信息
     *
     * @param token token
     * @return token用户信息
     */
    public SysUserToken getSysUserTokenByToken(String token) {
        String userId = RedisUtils.get(token);
        if(StringUtils.isEmpty(userId)){
            return null;
        }
        SysUserToken sysUserToken=new SysUserToken();
        sysUserToken.setToken(token);
        sysUserToken.setSysUserId(Long.parseLong(userId));
        return sysUserToken;
    }

    /**
     * 退出登录
     *
     * @param userId 用户id
     */
    public void logout(Long userId) {
        String userIdKey = RedisKeyConstants.MANAGE_SYS_USER_TOKEN + userId;
        String token = RedisUtils.get(userIdKey);
        String tokenKey = RedisKeyConstants.MANAGE_SYS_USER_TOKEN + token;
        RedisUtils.delete(userIdKey);
        RedisUtils.delete(tokenKey);
    }

    /**
     * 续期token
     *
     * @param userId 用户id
     * @param accessToken 新token
     */
    public void refreshToken(Long userId, String accessToken) {
        String tokenKey = RedisKeyConstants.MANAGE_SYS_USER_TOKEN + accessToken;
        String userIdKey = RedisKeyConstants.MANAGE_SYS_USER_TOKEN + userId;
        RedisUtils.updateExpire(tokenKey);
        RedisUtils.updateExpire(userIdKey);
    }

}
