package com.jinhx.blog.service.sys.impl;

import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.entity.sys.SysUser;
import com.jinhx.blog.entity.sys.SysUserToken;
import com.jinhx.blog.service.sys.SysUserMapperService;
import com.jinhx.blog.service.sys.ShiroService;
import com.jinhx.blog.service.sys.SysUserTokenService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ShiroServiceImpl
 *
 * @author jinhx
 * @since 2018-10-08
 */
@Service
public class ShiroServiceImpl implements ShiroService {

    @Autowired
    private SysUserMapperService sysUserMapperService;

    @Autowired
    private SysUserTokenService sysUserTokenService;

    /**
     * 获取用户的所有权限
     *
     * @param userId userId
     * @return Set<String>
     */
    @Override
    public Set<String> getUserPermissions(Integer userId) {
        List<String> permsList = sysUserMapperService.getAllPermsByUserId(userId);
        //返回用户权限列表
        return permsList.stream()
                // 过滤空置的字符串
                .filter(perms -> !StringUtils.isEmpty(perms))
                // 把小的list合并成大的list
                .flatMap(perms -> Arrays.stream(perms.split(",")))
                // 转换成set集合
                .collect(Collectors.toSet());
    }

    /**
     * 根据token查询token用户信息
     *
     * @param token token
     * @return token用户信息
     */
    @Override
    public SysUserToken getSysUserTokenByToken(String token) {
        return sysUserTokenService.getSysUserTokenByToken(RedisKeyConstants.MANAGE_SYS_USER_TOKEN+token);
    }

    /**
     * 根据用户id获取SysUserDTO
     *
     * @param userId 用户id
     * @return SysUserDTO
     */
    @Override
    public SysUser getSysUserDTOByUserId(Integer userId) {
        return sysUserMapperService.getSysUserDTOByUserId(userId);
    }

    /**
     * 续期token
     *
     * @param userId 用户id
     * @param accessToken 新token
     */
    @Override
    public void refreshToken(Integer userId, String accessToken) {
        sysUserTokenService.refreshToken(userId, accessToken);
    }

}
