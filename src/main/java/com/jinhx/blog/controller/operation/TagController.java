package com.jinhx.blog.controller.operation;

import com.jinhx.blog.common.util.MyAssert;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.common.validator.group.UpdateGroup;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.operation.Tag;
import com.jinhx.blog.entity.operation.vo.TagVO;
import com.jinhx.blog.service.operation.TagService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TagController
 *
 * @author jinhx
 * @since 2019-01-21
 */
@RestController
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 分页查询标签列表
     *
     * @param page page
     * @param limit limit
     * @param name name
     * @param module module
     * @return 标签列表
     */
    @GetMapping("/manage/operation/tag/list")
    @RequiresPermissions("operation:tag:list")
    public Response<PageData<Tag>> selectPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("name") String name, @RequestParam("module") Integer module){
        return Response.success(tagService.selectPage(page, limit, name, module));
    }

    /**
     * 根据模块查询标签列表
     *
     * @param module module
     * @return 标签列表
     */
    @GetMapping("/manage/operation/tag/select")
    @RequiresPermissions("operation:tag:list")
    public Response<List<Tag>> selectTagsByModule(@RequestParam("module") Integer module){
        return Response.success(tagService.selectTagsByModule(module));
    }

    /**
     * 根据tagId查询标签
     *
     * @param tagId tagId
     * @return 标签
     */
    @GetMapping("/manage/operation/tag/info/{tagId}")
    @RequiresPermissions("operation:tag:info")
    public Response<Tag> selectTagById(@PathVariable Long tagId){
        return Response.success(tagService.selectTagById(tagId));
    }

    /**
     * 新增标签
     *
     * @param tag tag
     * @return 新增结果
     */
    @PostMapping("/manage/operation/tag/save")
    @RequiresPermissions("operation:tag:save")
    public Response<Void> insertTag(@RequestBody Tag tag){
        ValidatorUtils.validateEntity(tag, InsertGroup.class);
        tagService.insertTag(tag);
        return Response.success();
    }

    /**
     * 根据tagId更新标签
     *
     * @param tag tag
     * @return 更新结果
     */
    @PutMapping("/manage/operation/tag/update")
    @RequiresPermissions("operation:tag:update")
    public Response<Void> updateTagById(@RequestBody Tag tag){
        ValidatorUtils.validateEntity(tag, UpdateGroup.class);
        tagService.updateTagById(tag);
        return Response.success();
    }

    /**
     * 批量根据tagId删除标签
     *
     * @param tagIds tagIds
     * @return 删除结果
     */
    @DeleteMapping("/manage/operation/tag/delete")
    @RequiresPermissions("operation:tag:delete")
    public Response<Void> deleteTagsById(@RequestBody List<Long> tagIds){
        MyAssert.sizeBetween(tagIds, 1, 100, "tagIds");
        tagService.deleteTagsById(tagIds);
        return Response.success();
    }

    /********************** portal ********************************/

    /**
     * 根据模块查询标签列表
     *
     * @param module 模块
     * @return 标签列表
     */
    @GetMapping("/operation/listtags")
    public Response<List<TagVO>> selectPortalTagVOsByModule(@RequestParam("module") Integer module) {
        MyAssert.notNull(module, "module不能为空");
        return Response.success(tagService.selectPortalTagVOsByModule(module));
    }

}
