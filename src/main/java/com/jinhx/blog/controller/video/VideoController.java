package com.jinhx.blog.controller.video;

import com.jinhx.blog.common.aop.annotation.LogView;
import com.jinhx.blog.common.util.MyAssert;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.common.validator.group.UpdateGroup;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.operation.VideoAdaptorBuilder;
import com.jinhx.blog.entity.video.Video;
import com.jinhx.blog.entity.video.vo.HomeVideoInfoVO;
import com.jinhx.blog.entity.video.vo.VideoVO;
import com.jinhx.blog.service.video.VideoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * VideoController
 *
 * @author jinhx
 * @since 2018-11-08
 */
@RestController
public class VideoController {

    @Autowired
    private VideoService videoService;

    /**
     * 查询首页信息
     *
     * @return 首页信息
     */
    @GetMapping("/manage/video/homeinfo")
    @RequiresPermissions("video:list")
    public Response<HomeVideoInfoVO> selectHommeVideoInfoVO() {
        return Response.success(videoService.selectHommeVideoInfoVO());
    }

    /**
     * 分页查询视频列表
     *
     * @param page 页码
     * @param limit 页数
     * @param title 标题
     * @return 视频列表
     */
    @GetMapping("/manage/video/list")
    @RequiresPermissions("video:list")
    public Response<PageData<VideoVO>> selectPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("title") String title) {
        return Response.success(videoService.selectPage(page, limit, title));
    }

    /**
     * 查询视频
     *
     * @param videoId videoId
     * @return 视频
     */
    @GetMapping("/manage/video/info/{videoId}")
    @RequiresPermissions("video:list")
    public Response<VideoVO> selectVideoVOByIdAndPublish(@PathVariable Long videoId) {
        return Response.success(videoService.selectVideoVOByIdAndPublish(videoId, null, new VideoAdaptorBuilder.Builder<Video>().setAll().build()));
    }

    /**
     * 新增视频
     *
     * @param videoVO 视频
     * @return 新增结果
     */
    @PostMapping("/manage/video/save")
    @RequiresPermissions("video:save")
    public Response<Void> insertVideo(@RequestBody VideoVO videoVO){
        ValidatorUtils.validateEntity(videoVO, InsertGroup.class);
        videoService.insertVideo(videoVO);
        return Response.success();
    }

    /**
     * 更新视频
     *
     * @param videoVO videoVO
     * @return 更新结果
     */
    @PutMapping("/manage/video/update")
    @RequiresPermissions("video:update")
    public Response<Void> updateVideo(@RequestBody VideoVO videoVO){
        ValidatorUtils.validateEntity(videoVO, UpdateGroup.class);
        videoService.updateVideo(videoVO);
        return Response.success();
    }

    /**
     * 更新视频状态
     *
     * @param videoVO videoVO
     * @return 更新结果
     */
    @PutMapping("/manage/video/update/status")
    @RequiresPermissions("video:update")
    public Response<Void> updateVideoStatus(@RequestBody VideoVO videoVO){
        videoService.updateVideoStatus(videoVO);
        return Response.success();
    }

    /**
     * 批量根据videoId删除视频
     *
     * @param videoIds videoIds
     * @return 删除结果
     */
    @DeleteMapping("/manage/video/delete")
    @RequiresPermissions("video:delete")
    public Response<Void> deleteVideosById(@RequestBody List<Long> videoIds) {
        MyAssert.sizeBetween(videoIds, 1, 100, "videoIds");
        videoService.deleteVideosById(videoIds);
        return Response.success();
    }

    /********************** portal ********************************/

    /**
     * 分页查询视频列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @param categoryId 分类
     * @param latest 时间排序
     * @param like 点赞量排序
     * @param watch 观看量排序
     * @return 视频列表
     */
    @GetMapping("/video/listvideos")
    @LogView(module = 1)
    public Response<PageData<VideoVO>> selectPortalPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit,
                               @RequestParam("latest") Boolean latest, @RequestParam("categoryId") Long categoryId,
                               @RequestParam("like") Boolean like, @RequestParam("watch") Boolean watch) {
        return Response.success(videoService.selectPortalPage(page, limit, latest, categoryId, like, watch));
    }

    /**
     * 根据videoId查询视频
     *
     * @param videoId videoId
     * @return 视频
     */
    @GetMapping("/video/{videoId}")
    @LogView(module = 1)
    public Response<VideoVO> selectPortalVideoVOById(@PathVariable Long videoId){
        return Response.success(videoService.selectPortalVideoVOById(videoId));
    }

    /**
     * 查询热观视频列表
     *
     * @return 热观视频列表
     */
    @GetMapping("/videos/listhotwatchvideos")
    @LogView(module = 1)
    public Response<List<VideoVO>> selectHotReadVideoVOs(){
        return Response.success(videoService.selectHotReadVideoVOs());
    }

    /**
     * 视频点赞
     *
     * @param videoId videoId
     * @return 点赞结果
     */
    @PutMapping("/video/{videoId}")
    @LogView(module = 1)
    public Response<Video> addVideoLikeNum(@PathVariable Long videoId) throws Exception{
        MyAssert.notNull(videoId, "videoId不能为空");
        videoService.addVideoLikeNum(videoId);
        return Response.success();
    }

}
