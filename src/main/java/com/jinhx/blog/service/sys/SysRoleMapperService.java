package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.SysAdminUtils;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.sys.SysRole;
import com.jinhx.blog.mapper.sys.SysRoleMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SysRoleMenuMapperService sysRoleMenuMapperService;

    @Autowired
    private SysUserRoleMapperService sysUserRoleMapperService;

    /**
     * 获取角色列表
     *
     * @param page 页码
     * @param limit 页数
     * @param roleName 角色名
     * @return 角色列表
     */
    public PageData queryPage(Integer page, Integer limit, String roleName) {
        IPage<SysRole> roleIPage = baseMapper.selectPage(new QueryPage<SysRole>(page, limit).getPage(),
                new LambdaQueryWrapper<SysRole>()
                        .like(StringUtils.isNotBlank(roleName), SysRole::getRoleName,roleName)
        );

        return new PageData(roleIPage);
    }

    /**
     * 根据角色id列表批量删除角色
     *
     * @param roleIds 角色id列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Integer[] roleIds) {
        if (SysAdminUtils.isHaveSuperAdmin(Lists.newArrayList(roleIds))){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "超级管理员角色不可以删除");
        }
        // 删除角色
        this.removeByIds(Lists.newArrayList(roleIds));

        // 删除角色与菜单关联
        sysRoleMenuMapperService.deleteBatchByRoleId(roleIds);

        // 删除角色与用户关联
        sysUserRoleMapperService.deleteBatchByRoleId(roleIds);
    }

    /**
     * 查询角色列表
     *
     * @param createrId 创建者id
     * @return 角色列表
     */
    public List<Integer> queryRoleIdList(Integer createrId) {
        return baseMapper.queryRoleIdList(createrId) ;
    }

    /**
     * 新增角色信息
     *
     * @param sysRole 角色信息
     * @return 新增结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean insertSysRole(SysRole sysRole) {
        baseMapper.insert(sysRole);

        // 保存角色与菜单关系
        sysRoleMenuMapperService.saveOrUpdate(sysRole.getId(), sysRole.getMenuIdList());
        return true;
    }

    /**
     * 更新角色信息
     *
     * @param sysRole 角色信息
     * @return 更新结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSysRoleById(SysRole sysRole){
        baseMapper.updateById(sysRole);

        // 保存角色与菜单关系
        sysRoleMenuMapperService.saveOrUpdate(sysRole.getId(), sysRole.getMenuIdList());
        return true;
    }

}
