package com.jinhx.blog.service.sys.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.common.util.SysAdminUtils;
import com.jinhx.blog.entity.sys.SysUserRole;
import com.jinhx.blog.mapper.sys.SysUserRoleMapper;
import com.jinhx.blog.service.sys.SysUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SysUserRoleServiceImpl
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    /**
     * 批量删除roleId
     *
     * @param roleIds roleIds
     */
    @Override
    public void deleteBatchByRoleId(Integer[] roleIds) {
        Arrays.stream(roleIds).forEach(roleId -> {
            baseMapper.delete(new UpdateWrapper<SysUserRole>().lambda()
            .eq(roleId!=null, SysUserRole::getRoleId,roleId));
        });
    }

    /**
     * 批量删除userId
     *
     * @param userIds userIds
     */
    @Override
    public void deleteBatchByUserId(Integer[] userIds) {
        Arrays.stream(userIds).forEach(userId -> {
            baseMapper.delete(new UpdateWrapper<SysUserRole>().lambda()
                    .eq(userId!=null, SysUserRole::getUserId,userId));
        });
    }

    /**
     * 更新或保存用户角色
     *
     * @param userId userId
     * @param roleIdList roleIdList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Integer userId, List<Integer> roleIdList) {
        // 先删除用户与角色关系
        baseMapper.delete(new UpdateWrapper<SysUserRole>().lambda()
                .eq(userId!=null, SysUserRole::getUserId,userId));

        if(roleIdList.size() == 0){
            return ;
        }

        // 保存用户与角色关系
        List<SysUserRole> list = new ArrayList<>(roleIdList.size());
        for(Integer roleId : roleIdList){
            SysUserRole SysUserRole = new SysUserRole();
            SysUserRole.setUserId(userId);
            SysUserRole.setRoleId(roleId);

            list.add(SysUserRole);
        }
        this.saveBatch(list);
    }

    /**
     * 根据用户id查询角色id列表
     *
     * @param userId 用户id
     * @return 角色id列表
     */
    @Override
    public List<Integer> getRoleIdListByUserId(Integer userId) {
        return baseMapper.getRoleIdListByUserId(userId);
    }

    /**
     * 根据userId查询roleName
     *
     * @param userId userId
     * @return List<String>
     */
    @Override
    public List<String> queryRoleNameList(Integer userId) {
        return baseMapper.queryRoleNameList(userId);
    }

    /**
     * 是否包含超级管理员
     *
     * @param userIds 用户id列表
     * @return 是否包含超级管理员
     */
    @Override
    public boolean isHaveSuperAdmin(Integer[] userIds) {
        return baseMapper.countSysUserRoleByRoleIdAndUserIds(userIds, SysAdminUtils.sysSuperAdminRoleId) > 0;
    }

}
