package com.jinhx.blog.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinhx.blog.entity.sys.SysUser;

import java.util.List;

/**
 * SysUserMapper
 *
 * @author jinhx
 * @since 2018-10-08
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户id查询用户有权限所有菜单列表
     *
     * @param sysUserId 用户id
     * @return 用户有权限所有菜单列表
     */
    List<String> selectAllPermsBySysUserId(Long sysUserId);

    /**
     * 根据用户id查询用户菜单列表
     *
     * @param sysUserId 用户id
     * @return 用户菜单列表
     */
    List<Long> selectSysMenuIdsBySysUserId(Long sysUserId);

}
