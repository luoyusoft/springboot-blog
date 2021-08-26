package com.jinhx.blog.service.sys;

import com.jinhx.blog.entity.sys.SysMenu;

import java.util.List;

/**
 * SysMenuService
 *
 * @author jinhx
 * @since 2018-10-22
 */
public interface SysMenuService {

    /**
     * 根据用户id查询用户的所有菜单列表
     *
     * @param sysUserId 用户id
     * @return 用户的所有菜单列表
     */
    List<SysMenu> selectSysMenusBySysUserId(Long sysUserId);

    /**
     * 根据sysMenuId查询菜单
     *
     * @param sysMenuId sysMenuId
     * @return 菜单
     */
    SysMenu selectSysMenuById(Long sysMenuId);

    /**
     * 查询不是按钮的菜单列表
     *
     * @return 菜单列表
     */
    List<SysMenu> selectNotButtonSysMenus();

    /**
     * 查询所有菜单列表
     *
     * @return 菜单列表
     */
    List<SysMenu> selectAllSysRoles();

    /**
     * 根据sysMenuId删除菜单
     *
     * @param sysMenuId sysMenuId
     */
    void deleteSysMenuById(Long sysMenuId);

    /**
     * 新增菜单
     *
     * @param sysMenu sysMenu
     */
    void insertSysMenu(SysMenu sysMenu);

    /**
     * 根据sysMenuId更新菜单
     *
     * @param sysMenu sysMenu
     */
    void updateSysMenuById(SysMenu sysMenu);

}
