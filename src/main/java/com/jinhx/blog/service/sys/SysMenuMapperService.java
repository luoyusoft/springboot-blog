package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.sys.SysMenu;
import com.jinhx.blog.mapper.sys.SysMenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 根据parentId查询菜单列表
     *
     * @param parentId parentId
     * @return 菜单列表
     */
    public List<SysMenu> selectSysMenusByParentId(Long parentId) {
        return baseMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, parentId)
                .orderByAsc(SysMenu::getOrderNum));
    }

    /**
     * 根据类型查询菜单列表
     *
     * @param type type
     * @return 菜单列表
     */
    public List<SysMenu> selectSysMenusByType(Integer type) {
        return baseMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .ne(SysMenu::getType, type)
                .orderByAsc(SysMenu::getOrderNum));
    }

    /**
     * 查询所有菜单列表
     *
     * @return 菜单列表
     */
    public List<SysMenu> selectAllSysMenus() {
        return baseMapper.selectList(new LambdaQueryWrapper<>());
    }

    /**
     * 根据sysMenuId查询菜单
     *
     * @param sysMenuId sysMenuId
     * @return 菜单
     */
    public SysMenu selectSysMenuById(Long sysMenuId) {
        List<SysMenu> sysMenus = selectSysMenusById(Lists.newArrayList(sysMenuId));
        if (CollectionUtils.isEmpty(sysMenus)){
            return null;
        }

        return sysMenus.get(0);
    }

    /**
     * 根据sysMenuId查询菜单列表
     *
     * @param sysMenuIds sysMenuIds
     * @return 菜单列表
     */
    public List<SysMenu> selectSysMenusById(List<Long> sysMenuIds) {
        if (CollectionUtils.isEmpty(sysMenuIds)){
            return Lists.newArrayList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<SysMenu>().in(SysMenu::getSysMenuId, sysMenuIds));
    }

    /**
     * 根据sysMenuId删除菜单
     *
     * @param sysMenuId sysMenuId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysMenuById(Long sysMenuId) {
        deleteSysMenusById(Lists.newArrayList(sysMenuId));
    }

    /**
     * 批量根据sysMenuId删除菜单
     *
     * @param sysMenuIds sysMenuIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysMenusById(List<Long> sysMenuIds) {
        if (CollectionUtils.isNotEmpty(sysMenuIds)){
            if (baseMapper.deleteBatchIds(sysMenuIds) != sysMenuIds.size()){
                throw new MyException(ResponseEnums.DELETE_FAIL);
            }
        }
    }

    /**
     * 新增菜单
     *
     * @param sysMenu sysMenu
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertSysMenu(SysMenu sysMenu) {
        insertSysMenus(Lists.newArrayList(sysMenu));
    }

    /**
     * 批量新增菜单
     *
     * @param sysMenus sysMenus
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertSysMenus(List<SysMenu> sysMenus) {
        if (CollectionUtils.isNotEmpty(sysMenus)){
            if (sysMenus.stream().mapToInt(item -> baseMapper.insert(item)).sum() != sysMenus.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

    /**
     * 根据sysMenuId更新菜单
     *
     * @param sysMenu sysMenu
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSysMenuById(SysMenu sysMenu) {
        updateSysMenusById(Lists.newArrayList(sysMenu));
    }

    /**
     * 批量根据sysMenuId更新菜单
     *
     * @param sysMenus sysMenus
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSysMenusById(List<SysMenu> sysMenus) {
        if (CollectionUtils.isNotEmpty(sysMenus)){
            if (sysMenus.stream().mapToInt(item -> baseMapper.updateById(item)).sum() != sysMenus.size()){
                throw new MyException(ResponseEnums.UPDATE_FAILR);
            }
        }
    }

}
