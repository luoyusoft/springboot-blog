package com.jinhx.blog.controller.sys;

import com.jinhx.blog.common.aop.annotation.SuperAdmin;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.SysAdminUtils;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.AddGroup;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.sys.SysRole;
import com.jinhx.blog.service.sys.SysRoleMenuService;
import com.jinhx.blog.service.sys.SysRoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    /**
     * 获取角色列表
     *
     * @param page 页码
     * @param limit 页数
     * @param roleName 角色名
     * @return 角色列表
     */
    @GetMapping("/manage/sys/role/list")
    @RequiresPermissions("sys:role:list")
    public Response list(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("roleName") String roleName){
        return Response.success(sysRoleService.queryPage(page, limit, roleName));
    }

    /**
     * 角色列表
     *
     * @return 角色列表
     */
    @GetMapping("/manage/sys/role/select")
    @RequiresPermissions("sys:role:select")
    public Response select(){
        Map<String, Object> map = new HashMap<>();

        Collection<SysRole> list = sysRoleService.listByMap(map);

        // 如果不是超级管理员，则不展示超级管理员
        if(!SysAdminUtils.isSuperAdmin()){
            return Response.success(list.stream().filter(item -> !item.getId().equals(SysAdminUtils.sysSuperAdminRoleId)).collect(Collectors.toList()));
        }

        return Response.success(list);
    }

    /**
     * 新增角色信息
     *
     * @param sysRole 角色信息
     * @return 新增结果
     */
    @SuperAdmin()
    @PostMapping("/manage/sys/role/save")
    @RequiresPermissions("sys:role:save")
    public Response insertSysRole(@RequestBody SysRole sysRole){
        ValidatorUtils.validateEntity(sysRole, AddGroup.class);
        sysRole.setCreaterId(SysAdminUtils.getUserId());

        sysRoleService.insertSysRole(sysRole);

        return Response.success();
    }

    /**
     * 更新角色信息
     *
     * @param sysRole 角色信息
     * @return 更新结果
     */
    @SuperAdmin()
    @PutMapping("/manage/sys/role/update")
    @RequiresPermissions("sys:role:update")
    public Response updateSysRoleById(@RequestBody SysRole sysRole){
        ValidatorUtils.validateEntity(sysRole, AddGroup.class);
        sysRole.setCreaterId(SysAdminUtils.getUserId());

        sysRoleService.updateSysRoleById(sysRole);

        return Response.success();
    }

    /**
     * 获取角色菜单列表
     *
     * @param roleId 角色id
     * @return 角色菜单列表
     */
    @GetMapping("/manage/sys/role/info/{roleId}")
    @RequiresPermissions("sys:role:info")
    public Response getSysRoleById(@PathVariable("roleId") Integer roleId){
        SysRole role = sysRoleService.getById(roleId);
        List<Integer> menuIdList=sysRoleMenuService.queryMenuIdList(roleId);
        role.setMenuIdList(menuIdList);

        return Response.success(role);
    }

    /**
     * 根据角色id列表批量删除角色
     *
     * @param roleIds 角色id列表
     */
    @SuperAdmin()
    @DeleteMapping("/manage/sys/role/delete")
    @RequiresPermissions("sys:role:delete")
    public Response delete(@RequestBody Integer[] roleIds){
        if (roleIds == null || roleIds.length < 1){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "roleIds不能为空");
        }

        if (roleIds.length > 100){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "roleIds不能超过100个");
        }

        sysRoleService.deleteBatch(roleIds);
        return Response.success();
    }

}
