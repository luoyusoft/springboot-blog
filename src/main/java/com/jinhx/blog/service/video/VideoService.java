package com.jinhx.blog.service.video;

import com.baomidou.mybatisplus.extension.service.IService;
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
public interface VideoService extends IService<Video> {

    /**
     * 将Video按需转换为VideoVO
     *
     * @param videoAdaptorBuilder videoAdaptorBuilder
     * @return VideoVO
     */
    VideoVO adaptorVideoToVideoVO(VideoAdaptorBuilder<Video> videoAdaptorBuilder);

    /**
     * 将VideoVO转换为Video
     *
     * @param videoAdaptorBuilder videoAdaptorBuilder
     * @return Video
     */
    Video adaptorVideoVOToVideo(VideoAdaptorBuilder<VideoVO> videoAdaptorBuilder);

    /**
     * 将Video列表按需转换为VideoVO列表
     *
     * @param videoAdaptorBuilder videoAdaptorBuilder
     * @return VideoVO列表
     */
    List<VideoVO> adaptorVideosToVideoVOs(VideoAdaptorBuilder<List<Video>> videoAdaptorBuilder);

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    HomeVideoInfoVO getHommeVideoInfoVO();

    /**
     * 分页查询视频列表
     *
     * @param page 页码
     * @param limit 页数
     * @param title 标题
     * @return 视频列表
     */
    PageData queryPage(Integer page, Integer limit, String title);

    /**
     * 保存视频
     *
     * @param videoVO videoVO
     */
    void saveVideo(VideoVO videoVO);

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
     * 获取视频对象
     *
     * @param videoId videoId
     * @param publish publish
     * @param videoAdaptorBuilder videoAdaptorBuilder
     * @return VideoVO
     */
    VideoVO getVideoVO(Long videoId, Boolean publish, VideoAdaptorBuilder<Video> videoAdaptorBuilder);

    /**
     * 判断类别下是否有视频
     *
     * @param categoryId categoryId
     * @return 类别下是否有视频
     */
    Boolean checkByCategoryId(Long categoryId);

    /**
     * 判断上传文件下是否有视频
     *
     * @param url url
     * @return 上传文件下是否有视频
     */
    Boolean checkByFile(String url);

    /**
     * 批量删除
     *
     * @param videoIds 视频id列表
     */
    void deleteVideos(List<Long> videoIds);

    /**
     * 查询所有已发布的视频
     *
     * @return 所有已发布的视频
     */
    List<Video> listVideosByPublish();

    /**
     * 根据标题查询所有已发布的视频
     *
     * @param title 标题
     * @return 所有已发布的视频
     */
    List<Video> listVideosByPublishAndTitle(String title);

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
    PageData listVideos(Integer page, Integer limit, Boolean latest, Long categoryId, Boolean like, Boolean watch);

    /**
     * 获取VideoVO
     *
     * @param id id
     * @return VideoVO
     */
    VideoVO getVideoVO(Long id);

    /**
     * 获取热观榜
     *
     * @return 热观视频列表
     */
    List<VideoVO> listHotWatchVideos();

    /**
     * 视频点赞
     *
     * @param id id
     * @return 点赞结果
     */
    Boolean updateVideo(Long id) throws Exception;

}
