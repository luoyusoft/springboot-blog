package com.jinhx.blog.controller.operation;

import com.jinhx.blog.common.util.MyAssert;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.common.validator.group.UpdateGroup;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.operation.Recommend;
import com.jinhx.blog.entity.operation.vo.HomeRecommendInfoVO;
import com.jinhx.blog.entity.operation.vo.RecommendVO;
import com.jinhx.blog.service.operation.RecommendService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * RecommendController
 *
 * @author jinhx
 * @since 2019-02-22
 */
@RestController
public class RecommendController {

    @Resource
    private RecommendService recommendService;

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    @GetMapping("/manage/operation/recommend/homeinfo")
    public Response<HomeRecommendInfoVO> selectHomeRecommendInfoVO() {
        return Response.success(recommendService.selectHomeRecommendInfoVO());
    }

    /**
     * 分页查询推荐列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @return 推荐列表
     */
    @GetMapping("/manage/operation/recommend/list")
    @RequiresPermissions("operation:recommend:list")
    public Response<PageData<RecommendVO>> selectPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit){
        return Response.success(recommendService.selectPage(page, limit));
    }

    /**
     * 根据模块，标题查询推荐列表
     *
     * @param module module
     * @param title title
     * @return 推荐列表
     */
    @GetMapping("/manage/operation/recommend/select")
    @RequiresPermissions("operation:recommend:list")
    public Response<List<RecommendVO>> selectRecommendVOsByModuleAndTitle(@RequestParam("module") Integer module, @RequestParam("title") String title) {
        MyAssert.notNull(module, "module不能为空");
        return Response.success(recommendService.selectRecommendVOsByModuleAndTitle(module, title));
    }

    /**
     * 根据recommendId查询推荐
     *
     * @param recommendId recommendId
     * @return 推荐
     */
    @GetMapping("/manage/operation/recommend/info/{id}")
    @RequiresPermissions("operation:recommend:info")
    public Response<Recommend> selectRecommendById(@PathVariable("id") Long recommendId){
        return Response.success(recommendService.selectRecommendById(recommendId));
    }

    /**
     * 新增推荐
     *
     * @param recommend recommend
     * @return 新增结果
     */
    @PostMapping("/manage/operation/recommend/save")
    @RequiresPermissions("operation:recommend:save")
    public Response<Void> insertRecommend(@RequestBody Recommend recommend){
        ValidatorUtils.validateEntity(recommend, InsertGroup.class);
        recommendService.insertRecommend(recommend);
        return Response.success();
    }

    /**
     * 根据linkId，模块更新推荐
     *
     * @param recommend recommend
     * @return 更新结果
     */
    @PutMapping("/manage/operation/recommend/update")
    @RequiresPermissions("operation:recommend:update")
    public Response<Void> updateRecommendByLinkIdAndModule(@RequestBody Recommend recommend){
        ValidatorUtils.validateEntity(recommend, UpdateGroup.class);
        recommendService.updateRecommendByLinkIdAndModule(recommend);
        return Response.success();
    }

    /**
     * 根据recommendId更新推荐置顶
     *
     * @param recommendId recommendId
     * @return 更新结果
     */
    @PutMapping("/manage/operation/recommend/top/{id}")
    @RequiresPermissions("operation:recommend:update")
    public Response<Void> updateRecommendToTopById(@PathVariable("id") Long recommendId){
        MyAssert.notNull(recommendId, "recommendId不能为空");
        recommendService.updateRecommendToTopById(recommendId);
        return Response.success();
    }

    /**
     * 批量根据friendLinkId删除推荐
     *
     * @param recommendIds recommendIds
     * @return 删除结果
     */
    @DeleteMapping("/manage/operation/recommend/delete")
    @RequiresPermissions("operation:recommend:delete")
    public Response<Void> deleteRecommendsById(@RequestBody List<Long> recommendIds){
        MyAssert.sizeBetween(recommendIds, 1, 100, "recommendIds");
        recommendService.deleteRecommendsById(recommendIds);
        return Response.success();
    }

    /********************** portal ********************************/

    /**
     * 根据模块查询推荐列表
     *
     * @param module 模块
     * @return 推荐列表
     */
    @RequestMapping("/operation/listrecommends")
    public Response<List<RecommendVO>> selectPortalRecommendVOsByModule(@RequestParam("module") Integer module) {
        MyAssert.notNull(module, "module不能为空");
        return Response.success(recommendService.selectPortalRecommendVOsByModule(module));
    }

}
