package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.entity.sys.SysRoleMenu;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SysRoleMenuService
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    /**
     * 保存角色与菜单关系
     *
     * @param roleId 角色id
     * @param menuIdList 菜单列表
     */
    void saveOrUpdate(Integer roleId, List<Integer> menuIdList);

    /**
     * 获取角色菜单列表
     *
     * @param roleId 角色id
     * @return 角色菜单列表
     */
    List<Integer> queryMenuIdList(Integer roleId);

    /**
     * 删除角色与菜单关联
     *
     * @param roleIds 角色id列表
     */
    void deleteBatchByRoleId(Integer[] roleIds);

}
