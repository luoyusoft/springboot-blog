package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.entity.sys.SysMenu;

import java.util.List;

/**
 * SysMenuService
 *
 * @author jinhx
 * @since 2018-10-22
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 获取用户的所有菜单列表
     *
     * @param userId 用户id
     * @return 用户的所有菜单列表
     */
    List<SysMenu> listUserMenu(Integer userId);

    /**
     * 根据父菜单，查询子菜单
     *
     * @param parentId 父菜单ID
     * @param menuIdList  用户菜单ID
     * @return List<SysMenu>
     */
    List<SysMenu> queryListParentId(Integer parentId, List<Integer> menuIdList);

    /**
     * 根据父菜单，查询子菜单
     *
     * @param parentId 父菜单ID
     * @return List<SysMenu>
     */
    List<SysMenu> queryListParentId(Integer parentId);

    /**
     * 获取不包含按钮的菜单列表
     *
     * @return List<SysMenu>
     */
    List<SysMenu> queryNotButtonList();

    /**
     * 获取用户菜单列表
     *
     * @param userId userId
     * @return List<SysMenu>
     */
    List<SysMenu> getUserMenuList(Integer userId);

    /**
     * 删除
     *
     * @param menuId menuId
     */
    void delete(Integer menuId);

}
