package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.sys.SysRoleMenu;
import com.jinhx.blog.mapper.sys.SysRoleMenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SysRoleMenuMapperService
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public class SysRoleMenuMapperService extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> {

    /**
     * 删除旧的角色菜单关联，新增心的角色菜单关联
     *
     * @param sysRoleId sysRoleId
     * @param sysMenuIds sysMenuIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteOldAndInsertNewSysRoleMenu(Long sysRoleId, List<Long> sysMenuIds) {
        // 先删除角色与菜单关系
        deleteSysRoleMenuBySysRoleId(sysRoleId);

        if (CollectionUtils.isNotEmpty(sysMenuIds)){
            // 保存角色与菜单关系
            List<SysRoleMenu> list = new ArrayList<>(sysMenuIds.size());
            for(Long sysMenuId : sysMenuIds){
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setSysMenuId(sysMenuId);
                sysRoleMenu.setSysRoleId(sysRoleId);
                list.add(sysRoleMenu);
            }
            insertSysRoleMenus(list);
        }
    }

    /**
     * 根据sysRoleId删除角色菜单关联
     *
     * @param sysRoleId sysRoleId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysRoleMenuBySysRoleId(Long sysRoleId) {
        baseMapper.delete(new LambdaUpdateWrapper<SysRoleMenu>().eq(SysRoleMenu::getSysRoleId, sysRoleId));
    }

    /**
     * 根据角色id查询菜单id列表
     *
     * @param sysRoleId sysRoleId
     * @return 菜单id列表
     */
    public List<Long> selectSysMenuIdsBySysRoleId(Long sysRoleId) {
        List<SysRoleMenu> sysRoleMenus = baseMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getSysRoleId, sysRoleId)
                .select(SysRoleMenu::getSysMenuId));

        if (CollectionUtils.isEmpty(sysRoleMenus)){
            return Lists.newArrayList();
        }

        return sysRoleMenus.stream().map(SysRoleMenu::getSysMenuId).collect(Collectors.toList());
    }

    /**
     * 批量根据sysRoleId删除角色菜单关联
     *
     * @param sysRoleIds sysRoleIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysRoleMenusBySysRoleId(List<Long> sysRoleIds) {
        baseMapper.delete(new LambdaUpdateWrapper<SysRoleMenu>().in(SysRoleMenu::getSysRoleId, sysRoleIds));
    }

    /**
     * 新增角色菜单关联
     *
     * @param sysRoleMenu sysRoleMenu
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertSysRoleMenu(SysRoleMenu sysRoleMenu) {
        insertSysRoleMenus(Lists.newArrayList(sysRoleMenu));
    }

    /**
     * 批量新增角色菜单关联
     *
     * @param sysRoleMenus sysRoleMenus
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertSysRoleMenus(List<SysRoleMenu> sysRoleMenus) {
        if (CollectionUtils.isNotEmpty(sysRoleMenus)){
            if (sysRoleMenus.stream().mapToInt(item -> baseMapper.insert(item)).sum() != sysRoleMenus.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

}
