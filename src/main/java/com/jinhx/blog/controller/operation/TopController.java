package com.jinhx.blog.controller.operation;

import com.jinhx.blog.common.util.MyAssert;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.common.validator.group.UpdateGroup;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.operation.Top;
import com.jinhx.blog.entity.operation.vo.TopVO;
import com.jinhx.blog.service.operation.TopService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TopController
 *
 * @author jinhx
 * @since 2019-02-22
 */
@RestController
public class TopController {

    @Autowired
    private TopService topService;

    /**
     * 分页查询置顶列表
     *
     * @param page page
     * @param limit limit
     * @return 置顶列表
     */
    @GetMapping("/manage/operation/top/list")
    @RequiresPermissions("operation:top:list")
    public Response<PageData<TopVO>> selectPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit){
        return Response.success(topService.selectPage(page, limit));
    }

    /**
     * 根据模块，标题查询置顶列表
     *
     * @param module module
     * @param title title
     * @return 置顶列表
     */
    @GetMapping("/manage/operation/top/select")
    @RequiresPermissions("operation:top:list")
    public Response<List<TopVO>> selectTopVOsByModuleAndTitle(@RequestParam("module") Integer module, @RequestParam("title") String title) {
        MyAssert.notNull(module, "module不能为空");
        return Response.success(topService.selectTopVOsByModuleAndTitle(module, title));
    }

    /**
     * 根据topId查询置顶
     *
     * @param topId topId
     * @return 置顶
     */
    @GetMapping("/manage/operation/top/info/{id}")
    @RequiresPermissions("operation:top:info")
    public Response<Top> selectTopById(@PathVariable("id") Long topId){
        return Response.success(topService.selectTopById(topId));
    }

    /**
     * 新增置顶
     *
     * @param top top
     * @return 新增结果
     */
    @PostMapping("/manage/operation/top/save")
    @RequiresPermissions("operation:top:save")
    public Response<Void> insertTop(@RequestBody Top top){
        ValidatorUtils.validateEntity(top, InsertGroup.class);
        topService.insertTop(top);
        return Response.success();
    }

    /**
     * 根据topId更新置顶
     *
     * @param top top
     * @return 更新结果
     */
    @PutMapping("/manage/operation/top/update")
    @RequiresPermissions("operation:top:update")
    public Response<Void> updateTopById(@RequestBody Top top){
        ValidatorUtils.validateEntity(top, UpdateGroup.class);
        topService.updateTopById(top);
        return Response.success();
    }

    /**
     * 根据topId进行置顶
     *
     * @param topId topId
     * @return 置顶结果
     */
    @PutMapping("/manage/operation/top/top/{id}")
    @RequiresPermissions("operation:top:update")
    public Response<Void> updateTopToTopById(@PathVariable("id") Long topId){
        MyAssert.notNull(topId, "topId不能为空");
        topService.updateTopToTopById(topId);
        return Response.success();
    }

    /**
     * 批量根据topId删除置顶
     *
     * @param topIds topIds
     * @return 删除结果
     */
    @DeleteMapping("/manage/operation/top/delete")
    @RequiresPermissions("operation:top:delete")
    public Response<Void> deleteTopsById(@RequestBody List<Long> topIds){
        MyAssert.sizeBetween(topIds, 1, 100, "topIds");
        topService.deleteTopsById(topIds);
        return Response.success();
    }

}
