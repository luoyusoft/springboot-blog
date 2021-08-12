package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.entity.sys.SysUserRole;

import java.util.List;

/**
 * SysUserRoleService
 *
 * @author jinhx
 * @since 2018-10-22
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    /**
     * 批量删除roleId
     *
     * @param roleIds roleIds
     */
    void deleteBatchByRoleId(Integer[] roleIds);

    /**
     * 批量删除userId
     *
     * @param userIds userIds
     */
    void deleteBatchByUserId(Integer[] userIds);

    /**
     * 更新或保存用户角色
     *
     * @param userId userId
     * @param roleIdList roleIdList
     */
    void saveOrUpdate(Integer userId, List<Integer> roleIdList);

    /**
     * 根据用户id查询角色id列表
     *
     * @param userId 用户id
     * @return 角色id列表
     */
    List<Integer> getRoleIdListByUserId(Integer userId);

    /**
     * 根据userId查询roleName
     *
     * @param userId userId
     * @return List<String>
     */
    List<String> queryRoleNameList(Integer userId);

    /**
     * 是否包含超级管理员
     *
     * @param userIds 用户id列表
     * @return 是否包含超级管理员
     */
    boolean isHaveSuperAdmin(Integer[] userIds);

}
