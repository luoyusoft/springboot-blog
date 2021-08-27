package com.jinhx.blog.controller.sys;

import com.jinhx.blog.common.aop.annotation.SuperAdmin;
import com.jinhx.blog.common.util.MyAssert;
import com.jinhx.blog.common.util.SysAdminUtils;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.common.validator.group.UpdateGroup;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.sys.SysRole;
import com.jinhx.blog.entity.sys.vo.SysRoleVO;
import com.jinhx.blog.service.sys.SysRoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SysRoleController
 *
 * @author jinhx
 * @since 2018-10-08
 */
@RestController
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 分别查询角色列表
     *
     * @param page 页码
     * @param limit 页数
     * @param roleName 角色名
     * @return 角色列表
     */
    @GetMapping("/manage/sys/role/list")
    @RequiresPermissions("sys:role:list")
    public Response<PageData<SysRole>> selectPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("roleName") String roleName){
        MyAssert.notNull(page, "page不能为空");
        MyAssert.notNull(limit, "limit不能为空");
        return Response.success(sysRoleService.selectPage(page, limit, roleName));
    }

    /**
     * 查询所有角色列表，如果不是超级管理员，则不展示超级管理员
     *
     * @return 角色列表
     */
    @GetMapping("/manage/sys/role/select")
    @RequiresPermissions("sys:role:select")
    public Response<List<SysRole>> selectAllSysRoles(){
        return Response.success(sysRoleService.selectAllSysRoles());
    }

    /**
     * 新增角色
     *
     * @param sysRoleVO sysRoleVO
     * @return 新增结果
     */
    @SuperAdmin()
    @PostMapping("/manage/sys/role/save")
    @RequiresPermissions("sys:role:save")
    public Response<Void> insertSysRole(@RequestBody SysRoleVO sysRoleVO){
        ValidatorUtils.validateEntity(sysRoleVO, InsertGroup.class);
        sysRoleVO.setCreaterId(SysAdminUtils.getSysUserId());
        sysRoleService.insertSysRole(sysRoleVO);
        return Response.success();
    }

    /**
     * 根据sysRoleId更新角色
     *
     * @param sysRoleVO sysRoleVO
     * @return 更新结果
     */
    @SuperAdmin()
    @PutMapping("/manage/sys/role/update")
    @RequiresPermissions("sys:role:update")
    public Response<Void> updateSysRoleById(@RequestBody SysRoleVO sysRoleVO){
        ValidatorUtils.validateEntity(sysRoleVO, UpdateGroup.class);
        sysRoleVO.setCreaterId(SysAdminUtils.getSysUserId());
        sysRoleService.updateSysRoleById(sysRoleVO);
        return Response.success();
    }

    /**
     * 根据sysRoleId查询角色
     *
     * @param sysRoleId sysRoleId
     * @return 角色
     */
    @GetMapping("/manage/sys/role/info/{sysRoleId}")
    @RequiresPermissions("sys:role:info")
    public Response<SysRoleVO> selectSysRoleVOById(@PathVariable Long sysRoleId){
        return Response.success(sysRoleService.selectSysRoleVOById(sysRoleId));
    }

    /**
     * 批量根据sysRoleId删除角色
     *
     * @param sysRoleIds sysRoleIds
     * @return 删除结果
     */
    @SuperAdmin()
    @DeleteMapping("/manage/sys/role/delete")
    @RequiresPermissions("sys:role:delete")
    public Response<Void> deleteSysRolesById(@RequestBody List<Long> sysRoleIds){
        MyAssert.sizeBetween(sysRoleIds, 1, 100, "sysRoleIds");
        sysRoleService.deleteSysRolesById(sysRoleIds);
        return Response.success();
    }

}
