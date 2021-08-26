package com.jinhx.blog.service.sys;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.sys.vo.SysUserVO;

import java.util.List;

/**
 * SysUserService
 *
 * @author jinhx
 * @since 2018-10-22
 */
public interface SysUserService {

    /**
     * 分页查询用户列表
     *
     * @param page 页码
     * @param limit 页数
     * @param username 用户名
     * @param sysUserId 用户id
     * @return 用户列表
     */
    PageData<SysUserVO> selectPage(Integer page, Integer limit, String username, Long sysUserId);

    /**
     * 根据用户id重置密码
     *
     * @param sysUserId 用户id
     * @param password 新密码
     */
    void resetPasswordById(Long sysUserId, String password);

    /**
     * 新增用户
     *
     * @param sysUserVO 用户信息
     * @return 初始密码
     */
    String insertSysUser(SysUserVO sysUserVO);

    /**
     * 根据sysUserId更新用户
     *
     * @param sysUserVO sysUserVO
     */
    void updateSysUserById(SysUserVO sysUserVO);

    /**
     * 批量根据sysUserId删除用户
     *
     * @param sysUserIds sysUserIds
     */
    void deleteSysUsersById(List<Long> sysUserIds);

    /**
     * 根据用户名获取SysUserDTO
     *
     * @param username 用户名
     * @return SysUserDTO
     */
    SysUserVO selectSysUserVOByUsername(String username);

    /**
     * 根据用户id查询SysUserVO
     *
     * @param sysUserId 用户id
     * @return SysUserVO
     */
    SysUserVO selectSysUserVOById(Long sysUserId);

}
