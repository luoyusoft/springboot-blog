package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.common.util.PageUtils;
import com.jinhx.blog.entity.sys.SysUser;
import com.jinhx.blog.entity.sys.dto.SysUserDTO;

import java.util.List;

/**
 * SysUserService
 * @author jinhx
 * @since 2018-10-08
 */
public interface SysUserMapperService extends IService<SysUser> {

    /**
     * 查询用户菜单列表
     *
     * @param userId 用户id
     * @return 用户菜单列表
     */
    List<Integer> queryAllMenuId(Integer userId);

    /**
     * 分页查询用户信息列表
     *
     * @param page 页码
     * @param limit 页数
     * @param username 用户名
     * @param id 用户id
     * @return 用户信息列表
     */
    PageUtils queryPage(Integer page, Integer limit, String username, Integer id);

    /**
     * 更新密码
     *
     * @param userId 用户id
     * @param password 旧密码
     * @param newPassword 新密码
     * @return 更新结果
     */
    boolean updatePassword(Integer userId, String password, String newPassword);

    /**
     * 重置密码
     *
     * @param userId 用户id
     * @param password 新密码
     * @return 重置结果
     */
    boolean resetPassword(Integer userId, String password);

    /**
     * 新增用户信息
     *
     * @param sysUserDTO 用户信息
     * @return 新增结果
     */
    boolean insertSysUser(SysUserDTO sysUserDTO);

    /**
     * 根据用户id更新用户信息
     *
     * @param sysUserDTO 用户信息
     * @return 更新结果
     */
    boolean updateSysUserById(SysUserDTO sysUserDTO);

    /**
     * 根据用户id列表批量删除用户
     *
     * @param userIds 用户id列表
     */
    boolean deleteBatch(Integer[] userIds);

    /**
     * 根据用户id查询用户有权限所有菜单列表
     *
     * @param userId 用户id
     * @return 用户有权限所有菜单列表
     */
    List<String> getAllPermsByUserId(Integer userId);

    /**
     * 根据用户名获取SysUserDTO
     *
     * @param username 用户名
     * @return SysUserDTO
     */
    SysUserDTO getSysUserDTOByUsername(String username);

    /**
     * 根据用户id获取SysUserDTO
     *
     * @param userId 用户id
     * @return SysUserDTO
     */
    SysUserDTO getSysUserDTOByUserId(Integer userId);

    /**
     * 根据用户id获取用户昵称
     *
     * @param userId 用户id
     * @return 用户昵称
     */
    String getNicknameByUserId(Integer userId);

}
