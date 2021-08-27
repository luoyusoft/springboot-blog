package com.jinhx.blog.service.sys;

import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.entity.sys.SysUserToken;
import com.jinhx.blog.entity.sys.vo.SysUserVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ShiroService
 *
 * @author jinhx
 * @since 2018-10-08
 */
@Service
public class ShiroService {

    @Autowired
    private SysUserMapperService sysUserMapperService;

    @Autowired
    private SysUserTokenService sysUserTokenService;

    @Autowired
    private SysUserService sysUserService;

    /**
     * 获取用户的所有权限
     *
     * @param sysUserId sysUserId
     * @return Set<String>
     */
    public Set<String> getUserPermissions(Long sysUserId) {
        List<String> permsList = sysUserMapperService.selectAllPermsBySysUserId(sysUserId);
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
    public SysUserToken getSysUserTokenByToken(String token) {
        return sysUserTokenService.getSysUserTokenByToken(RedisKeyConstants.MANAGE_SYS_USER_TOKEN+token);
    }

    /**
     * 根据用户id获取SysUser
     *
     * @param sysUserId 用户id
     * @return SysUser
     */
    public SysUserVO getSysUserVOBySysUserId(Long sysUserId) {
        return sysUserService.selectSysUserVOById(sysUserId);
    }

    /**
     * 续期token
     *
     * @param sysUserId 用户id
     * @param accessToken 新token
     */
    public void refreshToken(Long sysUserId, String accessToken) {
        sysUserTokenService.refreshToken(sysUserId, accessToken);
    }

}
