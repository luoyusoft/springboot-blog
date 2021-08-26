package com.jinhx.blog.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinhx.blog.entity.sys.SysUserRole;

import java.util.List;

/**
 * SysUserRoleMapper
 *
 * @author jinhx
 * @since 2018-10-08
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据sysUserId查询角色名列表
     *
     * @param sysUserId sysUserId
     * @return 角色名列表
     */
    List<String> selectRoleNamesBySysUserId(Long sysUserId);

}
