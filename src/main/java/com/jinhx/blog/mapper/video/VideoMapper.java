package com.jinhx.blog.mapper.video;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinhx.blog.entity.video.Video;
import com.jinhx.blog.entity.video.dto.VideoDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * VideoMapper
 *
 * @author jinhx
 * @since 2020-02-24
 */
public interface VideoMapper extends BaseMapper<Video> {

    /**
     * 获取已发布视频数量
     * @return 已发布视频数量
     */
    Integer selectPublishCount();

    /**
     * 获取所有视频数量
     * @return 所有视频数量
     */
    Integer selectAllCount();

    /**
     * 查询列表
     *
     * @param page
     * @param params
     * @return
     */
    List<VideoDTO> listVideoDTO(Page<VideoDTO> page, @Param("params") Map<String, Object> params);

    /**
     * 更新观看记录
     * @param id
     */
    Boolean updateWatchNum(Integer id);

    /**
     * 更新点赞
     * @param id
     */
    Boolean updateLikeNum(Integer id);

    /**
     * 判断类别下是否有视频
     * @param categoryId
     * @return
     */
    Integer checkByCategory(Integer categoryId);

    /**
     * 判断上传文件下是否有视频
     * @param url
     * @return
     */
    Integer checkByFile(String url);

    /**
     * 查询所有视频列表
     * @return
     */
    List<VideoDTO> selectVideoDTOList();

    /**
     * 查询所有视频列表
     * @return
     */
    List<Video> selectVideoListByTitle(String title);

    /**
     * 更新视频
     * @return
     */
    Boolean updateVideoById(Video video);

    /********************** portal ********************************/

    /**
     * 根据条件查询分页
     * @param page
     * @param params
     * @return
     */
    List<VideoDTO> queryPageCondition(Page<VideoDTO> page, @Param("params") Map<String, Object> params);

    /**
     * 获取简单的对象
     * @param id
     * @return
     */
    VideoDTO getSimpleVideoDTO(Integer id);

    /**
     * 获取热观榜
     * @return
     */
    List<VideoDTO> getHotWatchList();

    /**
     * 查询已发布视频
     * @return
     */
    Video selectVideoById(Integer id);

}
