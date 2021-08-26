package com.jinhx.blog.service.sys.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.SysAdminUtils;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.sys.SysRole;
import com.jinhx.blog.entity.sys.vo.SysRoleVO;
import com.jinhx.blog.service.sys.SysRoleMapperService;
import com.jinhx.blog.service.sys.SysRoleMenuMapperService;
import com.jinhx.blog.service.sys.SysRoleService;
import com.jinhx.blog.service.sys.SysUserRoleMapperService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * SysRoleServiceImpl
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMenuMapperService sysRoleMenuMapperService;

    @Autowired
    private SysUserRoleMapperService sysUserRoleMapperService;

    @Autowired
    private SysRoleMapperService sysRoleMapperService;

    /**
     * 分别查询角色列表
     *
     * @param page 页码
     * @param limit 页数
     * @param roleName 角色名
     * @return 角色列表
     */
    @Override
    public PageData<SysRole> selectPage(Integer page, Integer limit, String roleName) {
        return new PageData<>(sysRoleMapperService.selectPage(page, limit, roleName));
    }

    /**
     * 查询所有角色列表，如果不是超级管理员，则不展示超级管理员
     *
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectAllSysRoles() {
        List<SysRole> sysRoles = sysRoleMapperService.selectAllSysRoles();
        if(CollectionUtils.isEmpty(sysRoles)){
            return Lists.newArrayList();
        }

        // 如果不是超级管理员，则不展示超级管理员
        if(!SysAdminUtils.isSuperAdmin()){
            return sysRoles.stream().filter(item -> !item.getSysRoleId().equals(SysAdminUtils.sysSuperAdminRoleId)).collect(Collectors.toList());
        }
        return sysRoles;
    }

    /**
     * 根据sysRoleId查询角色
     *
     * @param sysRoleId sysRoleId
     * @return 角色
     */
    @Override
    public SysRoleVO selectSysRoleVOById(Long sysRoleId) {
        SysRole sysRole = sysRoleMapperService.selectSysRoleById(sysRoleId);
        if (Objects.isNull(sysRole)){
            return null;
        }

        SysRoleVO sysRoleVO = new SysRoleVO();
        BeanUtils.copyProperties(sysRole, sysRoleVO);

        List<Long> menuIdList = sysRoleMenuMapperService.selectSysMenuIdsBySysRoleId(sysRoleId);
        sysRoleVO.setMenuIdList(menuIdList);
        return sysRoleVO;
    }

    /**
     * 批量根据sysRoleId删除角色
     *
     * @param sysRoleIds sysRoleIds
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysRolesById(List<Long> sysRoleIds) {
        if (SysAdminUtils.isHaveSuperAdmin(sysRoleIds)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "超级管理员角色不可以删除");
        }
        // 删除角色
        sysRoleMapperService.deleteSysRolesById(sysRoleIds);

        // 删除角色与菜单关联
        sysRoleMenuMapperService.deleteSysRoleMenusBySysRoleId(sysRoleIds);

        // 删除角色与用户关联
        sysUserRoleMapperService.deleteSysUserRolesBySysRoleId(sysRoleIds);
    }

    /**
     * 新增角色
     *
     * @param sysRoleVO sysRoleVO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertSysRole(SysRoleVO sysRoleVO) {
        sysRoleMapperService.insertSysRole(sysRoleVO);

        // 保存角色与菜单关系
        sysRoleMenuMapperService.deleteOldAndInsertNewSysRoleMenu(sysRoleVO.getSysRoleId(), sysRoleVO.getMenuIdList());
    }

    /**
     * 根据sysRoleId更新角色
     *
     * @param sysRoleVO sysRoleVO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSysRoleById(SysRoleVO sysRoleVO){
        sysRoleMapperService.updateSysRoleById(sysRoleVO);

        //保存角色与菜单关系
        sysRoleMenuMapperService.deleteOldAndInsertNewSysRoleMenu(sysRoleVO.getSysRoleId(), sysRoleVO.getMenuIdList());
    }

}
