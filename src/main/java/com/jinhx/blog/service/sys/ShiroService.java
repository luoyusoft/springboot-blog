package com.jinhx.blog.service.sys;

import com.jinhx.blog.entity.sys.SysUser;
import com.jinhx.blog.entity.sys.SysUserToken;

import java.util.Set;

/**
 * ShiroService
 *
 * @author jinhx
 * @since 2018-10-08
 */
public interface ShiroService {

    /**
     * 获取用户的所有权限
     *
     * @param userId userId
     * @return Set<String>
     */
    Set<String> getUserPermissions(Integer userId);

    /**
     * 根据token查询token用户信息
     * @param token token
     * @return token用户信息
     */
    SysUserToken getSysUserTokenByToken(String token);

    /**
     * 根据用户id获取SysUserDTO
     * @param userId 用户id
     * @return SysUserDTO
     */
    SysUser getSysUserDTOByUserId(Integer userId);

    /**
     * 续期token
     * @param userId 用户id
     * @param accessToken 新token
     */
    void refreshToken(Integer userId, String accessToken);

}
