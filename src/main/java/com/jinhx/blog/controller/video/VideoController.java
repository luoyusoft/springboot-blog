package com.jinhx.blog.controller.video;

import com.jinhx.blog.common.aop.annotation.LogView;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.AddGroup;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.operation.VideoAdaptorBuilder;
import com.jinhx.blog.entity.video.Video;
import com.jinhx.blog.entity.video.vo.HomeVideoInfoVO;
import com.jinhx.blog.entity.video.vo.VideoVO;
import com.jinhx.blog.service.video.VideoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * VideoController
 *
 * @author jinhx
 * @since 2018-11-08
 */
@RestController
public class VideoController {

    @Resource
    private VideoService videoService;

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    @GetMapping("/manage/video/homeinfo")
    @RequiresPermissions("video:list")
    public Response getHommeVideoInfoVO() {
        HomeVideoInfoVO homeVideoInfoVO = videoService.getHommeVideoInfoVO();
        return Response.success(homeVideoInfoVO);
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
    public Response listVideo(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit, @RequestParam("title") String title) {
        PageData videoPage = videoService.queryPage(page, limit, title);
        return Response.success(videoPage);
    }

    /**
     * 获取视频对象
     *
     * @param videoId videoId
     * @return VideoVO
     */
    @GetMapping("/manage/video/info/{videoId}")
    @RequiresPermissions("video:list")
    public Response info(@PathVariable("videoId") Integer videoId) {
        return Response.success(videoService.getVideoVO(videoId, null, new VideoAdaptorBuilder.Builder<Video>().setAll().build()));
    }

    /**
     * 保存视频
     *
     * @param videoVO videoVO
     */
    @PostMapping("/manage/video/save")
    @RequiresPermissions("video:save")
    public Response saveVideo(@RequestBody VideoVO videoVO){
        ValidatorUtils.validateEntity(videoVO, AddGroup.class);
        videoService.saveVideo(videoVO);

        return Response.success();
    }

    /**
     * 更新视频
     *
     * @param videoVO videoVO
     */
    @PutMapping("/manage/video/update")
    @RequiresPermissions("video:update")
    public Response updateVideo(@RequestBody VideoVO videoVO){
        videoService.updateVideo(videoVO);
        return Response.success();
    }

    /**
     * 更新视频状态
     *
     * @param videoVO videoVO
     */
    @PutMapping("/manage/video/update/status")
    @RequiresPermissions("video:update")
    public Response updateVideoStatus(@RequestBody VideoVO videoVO){
        videoService.updateVideoStatus(videoVO);
        return Response.success();
    }

    /**
     * 批量删除
     *
     * @param ids 视频id数组
     */
    @DeleteMapping("/manage/video/delete")
    @RequiresPermissions("video:delete")
    public Response deleteVideos(@RequestBody Integer[] ids) {
        if (ids == null || ids.length < 1){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "ids不能为空");
        }

        if (ids.length > 100){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "ids不能超过100个");
        }

        videoService.deleteVideos(ids);
        return Response.success();
    }

    /********************** portal ********************************/

    /**
     * 分页获取视频列表
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
    public Response listVideos(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit,
                               @RequestParam("latest") Boolean latest, @RequestParam("categoryId") Integer categoryId,
                               @RequestParam("like") Boolean like, @RequestParam("watch") Boolean watch) {
        PageData queryPageCondition = videoService.listVideos(page, limit, latest, categoryId, like, watch);
        return Response.success(queryPageCondition);
    }

    /**
     * 获取VideoVO
     *
     * @param id id
     * @return VideoVO
     */
    @GetMapping("/video/{id}")
    @LogView(module = 1)
    public Response getVideo(@PathVariable Integer id){
        return Response.success(videoService.getVideoVO(id));
    }

    /**
     * 获取热观榜
     *
     * @return 热观视频列表
     */
    @GetMapping("/videos/listhotwatchvideos")
    @LogView(module = 1)
    public Response listHotWatchVideos(){
        return Response.success(videoService.listHotWatchVideos());
    }

    /**
     * 视频点赞
     *
     * @param id id
     * @return 点赞结果
     */
    @PutMapping("/video/{id}")
    @LogView(module = 1)
    public Response updateVideo(@PathVariable Integer id) throws Exception{
        if (id == null) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "id不能为空");
        }
        return Response.success(videoService.updateVideo(id));
    }

}
