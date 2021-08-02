package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.common.util.PageUtils;
import com.jinhx.blog.entity.sys.SysRole;

import java.util.List;

/**
 * SysRoleService
 *
 * @author luoyu
 * @date 2018/10/25 15:36
 * @description
 */
public interface SysRoleMapperService extends IService<SysRole> {

    /**
     * 获取角色列表
     *
     * @param page 页码
     * @param limit 页数
     * @param roleName 角色名
     * @return 角色列表
     */
    PageUtils queryPage(Integer page, Integer limit, String roleName);

    /**
     * 根据角色id列表批量删除角色
     *
     * @param roleIds 角色id列表
     */
    void deleteBatch(Integer[] roleIds);

    /**
     * 查询角色列表
     *
     * @param createrId 创建者id
     * @return 角色列表
     */
    List<Integer> queryRoleIdList(Integer createrId);

    /**
     * 新增角色信息
     *
     * @param sysRole 角色信息
     * @return 新增结果
     */
    boolean insertSysRole(SysRole sysRole);

    /**
     * 更新角色信息
     *
     * @param sysRole 角色信息
     * @return 更新结果
     */
    boolean updateSysRoleById(SysRole sysRole);

}
