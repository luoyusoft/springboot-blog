package com.jinhx.blog.service.sys;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.sys.SysRole;
import com.jinhx.blog.entity.sys.vo.SysRoleVO;

import java.util.List;

/**
 * SysRoleService
 *
 * @author jinhx
 * @since 2018-10-22
 */
public interface SysRoleService {

    /**
     * 分别查询角色列表
     *
     * @param page 页码
     * @param limit 页数
     * @param roleName 角色名
     * @return 角色列表
     */
    PageData<SysRole> selectPage(Integer page, Integer limit, String roleName);

    /**
     * 查询所有角色列表，如果不是超级管理员，则不展示超级管理员
     *
     * @return 角色列表
     */
    List<SysRole> selectAllSysRoles();

    /**
     * 根据sysRoleId查询角色
     *
     * @param sysRoleId sysRoleId
     * @return 角色
     */
    SysRoleVO selectSysRoleVOById(Long sysRoleId);

    /**
     * 批量根据sysRoleId删除角色
     *
     * @param sysRoleIds sysRoleIds
     */
    void deleteSysRolesById(List<Long> sysRoleIds);

    /**
     * 新增角色
     *
     * @param sysRoleVO sysRoleVO
     */
    void insertSysRole(SysRoleVO sysRoleVO);

    /**
     * 根据sysRoleId更新角色
     *
     * @param sysRoleVO sysRoleVO
     */
    void updateSysRoleById(SysRoleVO sysRoleVO);

}
