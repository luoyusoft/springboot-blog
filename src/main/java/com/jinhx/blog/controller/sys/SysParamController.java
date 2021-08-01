package com.jinhx.blog.controller.sys;

import com.jinhx.blog.common.aop.annotation.SuperAdmin;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.AddGroup;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.sys.SysParam;
import com.jinhx.blog.service.sys.SysParamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * SysParamController
 *
 * @author jinhx
 * @since 2018-10-08
 */
@RestController
@Slf4j
public class SysParamController {

    @Autowired
    private SysParamService paramService;

    /**
     * 分页查询
     *
     * @param page page
     * @param limit limit
     * @param menuUrl menuUrl
     * @param type type
     * @return PageUtils
     */
    @GetMapping("/manage/sys/param/list")
    @RequiresPermissions("sys:param:list")
    public Response list(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("menuUrl") String menuUrl, @RequestParam("type") String type){
        return Response.success(paramService.queryPage(page, limit, menuUrl, type));
    }

    /**
     * 获取所有参数
     *
     * @return 所有参数
     */
    @GetMapping("/manage/sys/param/all")
    public Response listAll(){
        return Response.success(paramService.list(null));
    }

    /**
     * 信息
     *
     * @param id id
     * @return 信息
     */
    @GetMapping("/manage/sys/param/info/{id}")
    @RequiresPermissions("sys:param:info")
    public Response info(@PathVariable("id") String id){
       return Response.success(paramService.getById(id));
    }

    /**
     * 保存
     *
     * @param param param
     */
    @PostMapping("/manage/sys/param/save")
    @RequiresPermissions("sys:param:save")
    public Response save(@RequestBody SysParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);
        paramService.save(param);

        return Response.success();
    }

    /**
     * 修改
     *
     * @param param param
     */
    @SuperAdmin()
    @PutMapping("/manage/sys/param/update")
    @RequiresPermissions("sys:param:update")
    public Response update(@RequestBody SysParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);
        paramService.updateById(param);

        return Response.success();
    }

    /**
     * 删除
     *
     * @param ids ids
     */
    @SuperAdmin()
    @DeleteMapping("/manage/sys/param/delete")
    @RequiresPermissions("sys:param:delete")
    public Response delete(@RequestBody String[] ids){
        if (ids == null || ids.length < 1){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "ids不能为空");
        }

        if (ids.length > 100){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "ids不能超过100个");
        }

        paramService.removeByIds(Arrays.asList(ids));
        return Response.success();
    }

}
