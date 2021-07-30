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
     * @param userId 用户id
     * @return 用户有权限所有菜单列表
     */
    List<String> getAllPermsByUserId(Integer userId);

    /**
     * 查询用户菜单列表
     * @param userId 用户id
     * @return 用户菜单列表
     */
    List<Integer> queryAllMenuId(Integer userId);

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getSysUserByUsername(String username);

    /**
     * 根据用户名查询用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    SysUser getSysUserByUserId(Integer userId);

    /**
     * 根据用户名查询用户个数
     * @param username 用户名
     * @return 用户个数
     */
    Integer countSysUserByUsername(String username);

    /**
     * 根据用户id获取用户昵称
     * @param userId 用户id
     * @return 用户昵称
     */
    String getNicknameByUserId(Integer userId);

}
