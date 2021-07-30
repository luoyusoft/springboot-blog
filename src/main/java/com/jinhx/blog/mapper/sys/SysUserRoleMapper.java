package com.jinhx.blog.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinhx.blog.entity.sys.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SysUserRoleMapper
 *
 * @author jinhx
 * @since 2018-10-08
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户id查询角色id列表
     * @param userId 用户id
     * @return 角色id列表
     */
    List<Integer> getRoleIdListByUserId(Integer userId);

    /**
     * 查询roleId
     * @param userId
     * @return
     */
    List<String> queryRoleNameList(Integer userId);

    /**
     * 根据用户id列表查询超级管理员个数
     * @param userIds 用户id列表
     * @return 超级管理员个数
     */
    Integer countSysUserRoleByRoleIdAndUserIds(@Param("userIds") Integer[] userIds, @Param("roleId") Integer roleId);

}
