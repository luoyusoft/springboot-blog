package com.jinhx.blog.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinhx.blog.entity.sys.SysRoleMenu;

import java.util.List;

/**
 * SysRoleMenuMapper
 *
 * @author jinhx
 * @since 2018-10-08
 */
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 根据roleId查询所属menuId
     * @param roleId
     * @return
     */
    List<Integer> queryMenuIdList(Integer roleId);

}
