package com.jinhx.blog.service.video;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.operation.VideoAdaptorBuilder;
import com.jinhx.blog.entity.video.Video;
import com.jinhx.blog.entity.video.vo.HomeVideoInfoVO;
import com.jinhx.blog.entity.video.vo.VideoVO;

import java.util.List;

/**
 * VideoService
 *
 * @author jinhx
 * @since 2018-11-22
 */
public interface VideoService {

    /**
     * 查询首页信息
     *
     * @return 首页信息
     */
    HomeVideoInfoVO selectHommeVideoInfoVO();

    /**
     * 分页查询视频列表
     *
     * @param page 页码
     * @param limit 页数
     * @param title 标题
     * @return 视频列表
     */
    PageData<VideoVO> selectPage(Integer page, Integer limit, String title);

    /**
     * 新增视频
     *
     * @param videoVO 视频
     */
    void insertVideo(VideoVO videoVO);

    /**
     * 更新视频
     *
     * @param videoVO videoVO
     */
    void updateVideo(VideoVO videoVO);

    /**
     * 更新视频状态
     *
     * @param videoVO videoVO
     */
    void updateVideoStatus(VideoVO videoVO);

    /**
     * 查询视频
     *
     * @param videoId videoId
     * @param publish publish
     * @param videoAdaptorBuilder videoAdaptorBuilder
     * @return 视频
     */
    VideoVO selectVideoVOByIdAndPublish(Long videoId, Boolean publish, VideoAdaptorBuilder<Video> videoAdaptorBuilder);

    /**
     * 批量根据videoId删除视频
     *
     * @param videoIds videoIds
     */
    void deleteVideosById(List<Long> videoIds);

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
    PageData<VideoVO> selectPortalPage(Integer page, Integer limit, Boolean latest, Long categoryId, Boolean like, Boolean watch);

    /**
     * 根据videoId查询视频
     *
     * @param videoId videoId
     * @return 视频
     */
    VideoVO selectPortalVideoVOById(Long videoId);

    /**
     * 查询热观视频列表
     *
     * @return 热观视频列表
     */
    List<VideoVO> selectHotReadVideoVOs();

    /**
     * 视频点赞
     *
     * @param videoId videoId
     */
    void addVideoLikeNum(Long videoId) throws Exception;

}
