package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.sys.SysRole;
import com.jinhx.blog.mapper.sys.SysRoleMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SysRoleMapperService
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public class SysRoleMapperService extends ServiceImpl<SysRoleMapper, SysRole> {

    /**
     * 分别查询角色列表
     *
     * @param page 页码
     * @param limit 页数
     * @param roleName 角色名
     * @return 角色列表
     */
    public IPage<SysRole> selectPage(Integer page, Integer limit, String roleName) {
        return baseMapper.selectPage(new QueryPage<SysRole>(page, limit).getPage(),
                new LambdaQueryWrapper<SysRole>()
                        .like(StringUtils.isNotBlank(roleName), SysRole::getRoleName, roleName)
        );
    }

    /**
     * 查询所有角色列表
     *
     * @return 角色列表
     */
    public List<SysRole> selectAllSysRoles() {
        return baseMapper.selectList(new LambdaQueryWrapper<>());
    }

    /**
     * 根据sysRoleId查询角色
     *
     * @param sysRoleId sysRoleId
     * @return 角色
     */
    public SysRole selectSysRoleById(Long sysRoleId) {
        List<SysRole> sysRoles = selectSysRolesById(Lists.newArrayList(sysRoleId));
        if (CollectionUtils.isEmpty(sysRoles)){
            return null;
        }

        return sysRoles.get(0);
    }

    /**
     * 根据sysRoleId查询角色列表
     *
     * @param sysRoleIds sysRoleIds
     * @return 角色列表
     */
    public List<SysRole> selectSysRolesById(List<Long> sysRoleIds) {
        if (CollectionUtils.isEmpty(sysRoleIds)){
            return Lists.newArrayList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getSysRoleId, sysRoleIds));
    }

    /**
     * 根据sysRoleId删除角色
     *
     * @param sysRoleId sysRoleId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysRoleById(Long sysRoleId) {
        deleteSysRolesById(Lists.newArrayList(sysRoleId));
    }

    /**
     * 批量根据sysRoleId删除角色
     *
     * @param sysRoleIds sysRoleIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysRolesById(List<Long> sysRoleIds) {
        if (CollectionUtils.isNotEmpty(sysRoleIds)){
            if (baseMapper.deleteBatchIds(sysRoleIds) != sysRoleIds.size()){
                throw new MyException(ResponseEnums.DELETE_FAIL);
            }
        }
    }

    /**
     * 新增角色
     *
     * @param sysRole sysRole
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertSysRole(SysRole sysRole) {
        insertSysRoles(Lists.newArrayList(sysRole));
    }

    /**
     * 批量新增角色
     *
     * @param sysRoles sysRoles
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertSysRoles(List<SysRole> sysRoles) {
        if (CollectionUtils.isNotEmpty(sysRoles)){
            if (sysRoles.stream().mapToInt(item -> baseMapper.insert(item)).sum() != sysRoles.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

    /**
     * 根据sysRoleId更新角色
     *
     * @param sysRole sysRole
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSysRoleById(SysRole sysRole) {
        updateSysRolesById(Lists.newArrayList(sysRole));
    }

    /**
     * 批量根据sysRoleId更新角色
     *
     * @param sysRoles sysRoles
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSysRolesById(List<SysRole> sysRoles) {
        if (CollectionUtils.isNotEmpty(sysRoles)){
            if (sysRoles.stream().mapToInt(item -> baseMapper.updateById(item)).sum() != sysRoles.size()){
                throw new MyException(ResponseEnums.UPDATE_FAILR);
            }
        }
    }

}
