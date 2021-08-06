package com.jinhx.blog.service.video;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.entity.video.Video;
import com.jinhx.blog.entity.video.vo.HomeVideoInfoVO;

import java.util.List;

/**
 * VideoMapperService
 *
 * @author jinhx
 * @since 2018-11-22
 */
public interface VideoMapperService extends IService<Video> {

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
    IPage<Video> queryPage(Integer page, Integer limit, String title);

    /**
     * 保存视频
     *
     * @param video video
     */
    void saveVideo(Video video);

    /**
     * 更新视频
     *
     * @param video video
     */
    void updateVideo(Video video);

    /**
     * 更新视频
     *
     * @param video video
     */
    void updateVideoById(Video video);

    /**
     * 获取视频对象
     *
     * @param videoId videoId
     * @param publish publish
     * @return Video
     */
    Video getVideo(Integer videoId, Boolean publish);

    /**
     * 判断类别下是否有视频
     *
     * @param categoryId categoryId
     * @return 类别下是否有视频
     */
    Boolean checkByCategoryId(Integer categoryId);

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
     * @param ids 视频id数组
     */
    void deleteVideos(List<Integer> ids);

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
    IPage<Video> listVideos(Integer page, Integer limit, Boolean latest, Integer categoryId, Boolean like, Boolean watch);

    /**
     * 获取热观榜
     *
     * @return 热观视频列表
     */
    List<Video> listHotWatchVideos();

}
