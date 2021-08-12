package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.common.enums.MenuTypeEnum;
import com.jinhx.blog.common.util.MapUtils;
import com.jinhx.blog.entity.sys.SysMenu;
import com.jinhx.blog.mapper.sys.SysMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 1、查询用户所属菜单
 * 2、递归构造Z-Tree需要格式的菜单
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public class SysMenuMapperService extends ServiceImpl<SysMenuMapper, SysMenu> {

    @Autowired
    private SysUserMapperService sysUserMapperService;

    @Autowired
    private SysRoleMenuMapperService sysRoleMenuMapperService;

    /**
     * 获取用户的所有菜单列表
     *
     * @param userId 用户id
     * @return 用户的所有菜单列表
     */
    public List<SysMenu> listUserMenu(Integer userId) {
        // 用户菜单列表
        List<Integer> menuIdList = sysUserMapperService.queryAllMenuId(userId);
        return getAllMenuList(menuIdList);
    }

    private List<SysMenu> getAllMenuList(List<Integer> menuIdList) {
        // 查询用户所属所有目录
        List<SysMenu> menuList = queryListParentId(0, menuIdList);
        // 递归生成特定格式的菜单列表
        getMenuTreeList(menuList, menuIdList);

        return menuList;
    }
    
    /**
     * 递归
     *
     * @param menuList menuList
     * @param menuIdList menuIdList
     * @return List<SysMenu>
     */
    private List<SysMenu> getMenuTreeList(List<SysMenu> menuList, List<Integer> menuIdList){
        List<SysMenu> subMenuList = new ArrayList<>();

        for(SysMenu entity : menuList){
            // 目录
            if(entity.getType() == MenuTypeEnum.CATALOG.getCode()){
                entity.setList(getMenuTreeList(queryListParentId(entity.getId(), menuIdList), menuIdList));
            }
            subMenuList.add(entity);
        }

        return subMenuList;
    }

    /**
     * 根据父菜单，查询子菜单，用于鉴权
     *
     * @param parentId 父菜单ID
     * @param menuIdList  用户菜单ID
     * @return List<SysMenu>
     */
    public List<SysMenu> queryListParentId(Integer parentId, List<Integer> menuIdList) {
        List<SysMenu> menuList = queryListParentId(parentId);
        if(CollectionUtils.isEmpty(menuList)){
            return menuList;
        }

        List<SysMenu> userMenuList = new ArrayList<>();
        for(SysMenu menu : menuList){
            if(menuIdList.contains(menu.getId())){
                userMenuList.add(menu);
            }
        }
        return userMenuList;
    }

    /**
     * 根据父菜单，查询子菜单
     *
     * @param parentId 父菜单ID
     * @return List<SysMenu>
     */
    public List<SysMenu> queryListParentId(Integer parentId) {
        return baseMapper.queryListParentId(parentId);
    }

    /**
     * 获取不包含按钮的菜单列表
     *
     * @return List<SysMenu>
     */
    public List<SysMenu> queryNotButtonList() {
        return baseMapper.queryNotButtonList();
    }

    /**
     * 获取用户菜单列表
     *
     * @param userId userId
     * @return List<SysMenu>
     */
    public List<SysMenu> getUserMenuList(Integer userId) {
        // 用户菜单列表
        List<Integer> menuIdList = sysUserMapperService.queryAllMenuId(userId);
        return getAllMenuList(menuIdList);
    }

    /**
     * 删除
     *
     * @param menuId menuId
     */
    public void delete(Integer menuId) {
        // 删除菜单
        baseMapper.deleteById(menuId);
        // 删除菜单与角色关联
        sysRoleMenuMapperService.removeByMap(new MapUtils().put("menu_id",menuId));
    }

}
