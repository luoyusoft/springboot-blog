package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.entity.sys.SysRoleMenu;
import com.jinhx.blog.mapper.sys.SysRoleMenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SysRoleMenuMapperService
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public class SysRoleMenuMapperService extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> {

    /**
     * 保存角色与菜单关系
     *
     * @param roleId 角色id
     * @param menuIdList 菜单列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Integer roleId, List<Integer> menuIdList) {
        //先删除角色与菜单关系
        baseMapper.delete(new UpdateWrapper<SysRoleMenu>().lambda()
                .eq(roleId!=null, SysRoleMenu::getRoleId,roleId));

        if(menuIdList.size() == 0){
            return ;
        }

        //保存角色与菜单关系
        List<SysRoleMenu> list = new ArrayList<>(menuIdList.size());
        for(Integer menuId : menuIdList){
            SysRoleMenu SysRoleMenu = new SysRoleMenu();
            SysRoleMenu.setMenuId(menuId);
            SysRoleMenu.setRoleId(roleId);

            list.add(SysRoleMenu);
        }
        this.saveBatch(list);
    }

    /**
     * 获取角色菜单列表
     *
     * @param roleId 角色id
     * @return 角色菜单列表
     */
    public List<Integer> queryMenuIdList(Integer roleId) {
        return baseMapper.queryMenuIdList(roleId);
    }

    /**
     * 删除角色与菜单关联
     *
     * @param roleIds 角色id列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchByRoleId(Integer[] roleIds) {
        Arrays.stream(roleIds).forEach(roleId -> {
            baseMapper.delete(new UpdateWrapper<SysRoleMenu>().lambda()
                    .eq(roleId!=null, SysRoleMenu::getRoleId,roleId));
        });
    }

}
