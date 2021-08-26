package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.sys.SysUserRole;
import com.jinhx.blog.mapper.sys.SysUserRoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SysUserRoleMapperService
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public class SysUserRoleMapperService extends ServiceImpl<SysUserRoleMapper, SysUserRole> {

    /**
     * 批量根据sysRoleId删除用户角色关联
     *
     * @param sysRoleIds sysRoleIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysUserRolesBySysRoleId(List<Long> sysRoleIds) {
        baseMapper.delete(new LambdaUpdateWrapper<SysUserRole>().in(SysUserRole::getSysRoleId, sysRoleIds));
    }

    /**
     * 批量根据sysUserId删除用户角色关联
     *
     * @param sysUserIds sysUserIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysUserRolesBySysUserId(List<Long> sysUserIds) {
        baseMapper.delete(new LambdaUpdateWrapper<SysUserRole>().in(SysUserRole::getSysUserId, sysUserIds));
    }

    /**
     * 删除旧的用户角色关联，新增心的用户角色关联
     *
     * @param sysUserId sysUserId
     * @param sysRoleIds sysRoleIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteOldAndInsertNewSysUserRole(Long sysUserId, List<Long> sysRoleIds) {
        // 先删除用户与角色关系
        deleteSysUserRoleBySysUserId(sysUserId);

        if (CollectionUtils.isNotEmpty(sysRoleIds)){
            // 保存用户与角色关系
            List<SysUserRole> list = new ArrayList<>(sysRoleIds.size());
            for(Long sysRoleId : sysRoleIds){
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setSysUserId(sysUserId);
                sysUserRole.setSysRoleId(sysRoleId);
                list.add(sysUserRole);
            }
            this.insertSysUserRoles(list);
        }
    }

    /**
     * 根据sysUserId删除用户角色关联
     *
     * @param sysUserId sysUserId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysUserRoleBySysUserId(Long sysUserId) {
        baseMapper.delete(new LambdaUpdateWrapper<SysUserRole>().eq(SysUserRole::getSysUserId, sysUserId));
    }

    /**
     * 新增用户角色关联
     *
     * @param sysUserRole sysUserRole
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertSysUserRole(SysUserRole sysUserRole) {
        insertSysUserRoles(Lists.newArrayList(sysUserRole));
    }

    /**
     * 批量新增用户角色关联
     *
     * @param sysUserRoles sysUserRoles
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertSysUserRoles(List<SysUserRole> sysUserRoles) {
        if (CollectionUtils.isNotEmpty(sysUserRoles)){
            if (sysUserRoles.stream().mapToInt(item -> baseMapper.insert(item)).sum() != sysUserRoles.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

    /**
     * 根据用户id查询角色id列表
     *
     * @param sysUserId 用户id
     * @return 角色id列表
     */
    public List<Long> selectSysRoleIdsBySysUserId(Long sysUserId) {
        List<SysUserRole> sysUserRoles = baseMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getSysUserId, sysUserId)
                .select(SysUserRole::getSysRoleId));

        if (CollectionUtils.isEmpty(sysUserRoles)){
            return Lists.newArrayList();
        }

        return sysUserRoles.stream().map(SysUserRole::getSysRoleId).collect(Collectors.toList());
    }

    /**
     * 根据sysUserId查询角色名列表
     *
     * @param sysUserId sysUserId
     * @return 角色名列表
     */
    public List<String> selectRoleNamesBySysUserId(Long sysUserId) {
        return baseMapper.selectRoleNamesBySysUserId(sysUserId);
    }

    /**
     * 根据sysUserId，sysUserIds查询用户角色关联数量
     *
     * @param sysUserIds sysUserIds
     * @param sysRoleId sysRoleId
     * @return 用户角色关联数量
     */
    public Integer selectSysUserRoleCountBySysUserIdAndSysRoleId(List<Long> sysUserIds, Long sysRoleId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getSysRoleId, sysRoleId)
                .in(SysUserRole::getSysUserId, sysUserIds));
    }

}
