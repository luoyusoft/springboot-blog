package com.jinhx.blog.controller.operation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.AddGroup;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.operation.Tag;
import com.jinhx.blog.entity.operation.TagLink;
import com.jinhx.blog.entity.operation.vo.TagVO;
import com.jinhx.blog.mapper.operation.TagLinkMapper;
import com.jinhx.blog.service.operation.TagService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * TagController
 *
 * @author jinhx
 * @since 2019-01-21
 */
@RestController
public class TagController {

    @Resource
    private TagService tagService;

    @Resource
    private TagLinkMapper tagLinkMapper;

    /**
     * 分页查询
     *
     * @param page page
     * @param limit limit
     * @param name name
     * @param module module
     * @return PageUtils
     */
    @GetMapping("/manage/operation/tag/list")
    @RequiresPermissions("operation:tag:list")
    public Response list(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("name") String name, @RequestParam("module") Integer module){
        PageData tagPage = tagService.queryPage(page, limit, name, module);
        return Response.success(tagPage);
    }

    /**
     * 列表
     *
     * @param module module
     * @return 列表
     */
    @GetMapping("/manage/operation/tag/select")
    @RequiresPermissions("operation:tag:list")
    public Response select(@RequestParam("module") Integer module){
        List<Tag> tagList = tagService.list(new QueryWrapper<Tag>().lambda().eq(module != null,Tag::getModule,module));
        return Response.success(tagList);
    }

    /**
     * 信息
     *
     * @param id id
     * @return 信息
     */
    @GetMapping("/manage/operation/tag/info/{id}")
    @RequiresPermissions("operation:tag:info")
    public Response info(@PathVariable("id") String id){
       Tag tag = tagService.getById(id);
        return Response.success(tag);
    }

    /**
     * 保存
     *
     * @param tag tag
     */
    @PostMapping("/manage/operation/tag/save")
    @RequiresPermissions("operation:tag:save")
    public Response save(@RequestBody Tag tag){
        ValidatorUtils.validateEntity(tag, AddGroup.class);
        tagService.save(tag);

        return Response.success();
    }

    /**
     * 修改
     *
     * @param tag tag
     */
    @PutMapping("/manage/operation/tag/update")
    @RequiresPermissions("operation:tag:update")
    public Response update(@RequestBody Tag tag){
        ValidatorUtils.validateEntity(tag, AddGroup.class);
        tagService.updateById(tag);

        return Response.success();
    }

    /**
     * 删除
     *
     * @param ids ids
     */
    @DeleteMapping("/manage/operation/tag/delete")
    @RequiresPermissions("operation:tag:delete")
    public Response delete(@RequestBody String[] ids){
        if (ids == null || ids.length < 1){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "ids不能为空");
        }

        if (ids.length > 100){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "ids不能超过100个");
        }

        for (String id : ids) {
            List<TagLink> tagLinkList = tagLinkMapper.selectList(new QueryWrapper<TagLink>().lambda().eq(TagLink::getTagId, id));
            if(!CollectionUtils.isEmpty(tagLinkList)) {
                TagLink tagLink = tagLinkList.get(0);
                if (tagLink.getModule().equals(ModuleTypeConstants.ARTICLE)) {
                    throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该标签下有文章，无法删除");
                }
            }
        }
        tagService.removeByIds(Lists.newArrayList(ids));

        return Response.success();
    }

    /********************** portal ********************************/

    /**
     * 获取标签列表
     *
     * @param module 模块
     * @return 标签列表
     */
    @GetMapping("/operation/listtags")
    public Response listTags(@RequestParam("module") Integer module) {
        if (module == null){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "module不能为空");
        }
        List<TagVO> tagList = tagService.listTags(module);
        return Response.success(tagList);
    }

}
