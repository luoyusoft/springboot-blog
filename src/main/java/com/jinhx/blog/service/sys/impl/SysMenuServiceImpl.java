package com.jinhx.blog.service.sys.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.MenuTypeEnum;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.MapUtils;
import com.jinhx.blog.entity.sys.SysMenu;
import com.jinhx.blog.service.sys.SysMenuMapperService;
import com.jinhx.blog.service.sys.SysMenuService;
import com.jinhx.blog.service.sys.SysRoleMenuMapperService;
import com.jinhx.blog.service.sys.SysUserMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 1、查询用户所属菜单
 * 2、递归构造Z-Tree需要格式的菜单
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysUserMapperService sysUserMapperService;

    @Autowired
    private SysMenuMapperService sysMenuMapperService;

    @Autowired
    private SysRoleMenuMapperService sysRoleMenuMapperService;

    /**
     * 根据用户id查询用户的所有菜单列表
     *
     * @param sysUserId 用户id
     * @return 用户的所有菜单列表
     */
    @Override
    public List<SysMenu> selectSysMenusBySysUserId(Long sysUserId) {
        // 用户菜单列表
        List<Long> sysMenuIds = sysUserMapperService.selectSysMenuIdsBySysUserId(sysUserId);
        return selectAllSysMenus(sysMenuIds);
    }

    /**
     * 根据sysMenuId查询菜单
     *
     * @param sysMenuId sysMenuId
     * @return 菜单
     */
    @Override
    public SysMenu selectSysMenuById(Long sysMenuId) {
        return sysMenuMapperService.selectSysMenuById(sysMenuId);
    }

    /**
     * 完善菜单列表
     *
     * @param sysMenuIds sysMenuIds
     * @return 菜单列表
     */
    private List<SysMenu> selectAllSysMenus(List<Long> sysMenuIds) {
        // 查询用户所属所有目录
        List<SysMenu> sysMenus = selectSysMenusByParentIdAndSysMenuId(0L, sysMenuIds);
        // 递归生成特定格式的菜单列表
        getMenuTreeList(sysMenus, sysMenuIds);

        return sysMenus;
    }

    /**
     * 递归
     *
     * @param sysMenus sysMenus
     * @param sysMenuIds sysMenuIds
     * @return List<SysMenu>
     */
    private List<SysMenu> getMenuTreeList(List<SysMenu> sysMenus, List<Long> sysMenuIds){
        List<SysMenu> subMenuList = new ArrayList<>();

        for(SysMenu sysMenu : sysMenus){
            // 目录
            if(sysMenu.getType() == MenuTypeEnum.CATALOG.getCode()){
                sysMenu.setList(getMenuTreeList(selectSysMenusByParentIdAndSysMenuId(sysMenu.getSysMenuId(), sysMenuIds), sysMenuIds));
            }
            subMenuList.add(sysMenu);
        }

        return subMenuList;
    }

    /**
     * 根据parentId，menuIds查询子菜单，用于鉴权
     *
     * @param parentId 父菜单id
     * @param sysMenuIds  用户菜单id
     * @return List<SysMenu>
     */
    private List<SysMenu> selectSysMenusByParentIdAndSysMenuId(Long parentId, List<Long> sysMenuIds) {
        List<SysMenu> sysMenus = sysMenuMapperService.selectSysMenusByParentId(parentId);
        if(CollectionUtils.isEmpty(sysMenus)){
            return sysMenus;
        }

        List<SysMenu> userMenuList = new ArrayList<>();
        for(SysMenu sysMenu : sysMenus){
            if(sysMenuIds.contains(sysMenu.getSysMenuId())){
                userMenuList.add(sysMenu);
            }
        }
        return userMenuList;
    }

    /**
     * 查询不是按钮的菜单列表
     *
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectNotButtonSysMenus() {
        return sysMenuMapperService.selectSysMenusByType(SysMenu.TYPE_BUTTON);
    }

    /**
     * 查询所有菜单列表
     *
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectAllSysRoles() {
        List<SysMenu> sysMenus = sysMenuMapperService.selectAllSysRoles();
        if (CollectionUtils.isEmpty(sysMenus)){
            return Lists.newArrayList();
        }

        sysMenus.forEach(sysMenu -> {
            SysMenu parentSysMenu = sysMenuMapperService.selectSysMenuById(sysMenu.getParentId());
            if(Objects.nonNull(parentSysMenu)){
                sysMenu.setParentName(parentSysMenu.getName());
            }
        });
        return sysMenuMapperService.selectAllSysRoles();
    }

    /**
     * 根据sysMenuId删除菜单
     *
     * @param sysMenuId sysMenuId
     */
    @Override
    public void deleteSysMenuById(Long sysMenuId) {
        //判断是否有子菜单或按钮
        List<SysMenu> menuList = sysMenuMapperService.selectSysMenusByParentId(sysMenuId);
        if(CollectionUtils.isNotEmpty(menuList)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "请先删除子菜单或按钮");
        }
        // 删除菜单
        sysMenuMapperService.deleteSysMenuById(sysMenuId);
        // 删除菜单与角色关联
        sysRoleMenuMapperService.removeByMap(new MapUtils().put("sys_menu_id",sysMenuId));
    }

    /**
     * 新增菜单
     *
     * @param sysMenu sysMenu
     */
    @Override
    public void insertSysMenu(SysMenu sysMenu) {
        sysMenuMapperService.insertSysMenu(sysMenu);
    }

    /**
     * 根据sysMenuId更新菜单
     *
     * @param sysMenu sysMenu
     */
    @Override
    public void updateSysMenuById(SysMenu sysMenu) {
        sysMenuMapperService.updateSysMenuById(sysMenu);
    }

}
