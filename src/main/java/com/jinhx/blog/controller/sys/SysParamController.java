package com.jinhx.blog.controller.sys;

import com.jinhx.blog.common.aop.annotation.SuperAdmin;
import com.jinhx.blog.common.util.MyAssert;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.common.validator.group.UpdateGroup;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.sys.SysParam;
import com.jinhx.blog.service.sys.SysParamService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SysParamController
 *
 * @author jinhx
 * @since 2018-10-08
 */
@RestController
public class SysParamController {

    @Autowired
    private SysParamService sysParamService;

    /**
     * 分页查询系统参数列表
     *
     * @param page page
     * @param limit limit
     * @param menuUrl menuUrl
     * @param type type
     * @return 系统参数列表
     */
    @GetMapping("/manage/sys/param/list")
    @RequiresPermissions("sys:param:list")
    public Response<PageData<SysParam>> selectPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("menuUrl") String menuUrl, @RequestParam("type") String type){
        return Response.success(sysParamService.selectPage(page, limit, menuUrl, type));
    }

    /**
     * 查询所有参数列表
     *
     * @return 参数列表
     */
    @GetMapping("/manage/sys/param/all")
    public Response<List<SysParam>> selectAllSysParams(){
        return Response.success(sysParamService.selectAllSysParams());
    }

    /**
     * 根据sysParamId查询参数
     *
     * @param sysParamId sysParamId
     * @return 参数
     */
    @GetMapping("/manage/sys/param/info/{id}")
    @RequiresPermissions("sys:param:info")
    public Response<SysParam> selectSysParamById(@PathVariable("id") Long sysParamId){
       return Response.success(sysParamService.selectSysParamById(sysParamId));
    }

    /**
     * 新增参数
     *
     * @param sysParam sysParam
     * @return 新增结果
     */
    @PostMapping("/manage/sys/param/save")
    @RequiresPermissions("sys:param:save")
    public Response<Void> insertSysParam(@RequestBody SysParam sysParam){
        ValidatorUtils.validateEntity(sysParam, InsertGroup.class);
        sysParamService.insertSysParam(sysParam);
        return Response.success();
    }

    /**
     * 根据sysParamId更新参数
     *
     * @param sysParam sysParam
     * @return 更新结果
     */
    @SuperAdmin()
    @PutMapping("/manage/sys/param/update")
    @RequiresPermissions("sys:param:update")
    public Response<Void> updateSysParamById(@RequestBody SysParam sysParam){
        ValidatorUtils.validateEntity(sysParam, UpdateGroup.class);
        sysParamService.updateSysParamById(sysParam);
        return Response.success();
    }

    /**
     * 批量根据sysParamId删除参数
     *
     * @param sysParamIds sysParamIds
     * @return 删除结果
     */
    @SuperAdmin()
    @DeleteMapping("/manage/sys/param/delete")
    @RequiresPermissions("sys:param:delete")
    public Response<Void> delete(@RequestBody List<Long> sysParamIds){
        MyAssert.sizeBetween(sysParamIds, 1, 100, "sysParamIds");
        sysParamService.deleteSysParamsById(sysParamIds);
        return Response.success();
    }

}
