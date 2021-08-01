package com.jinhx.blog.service.search;

import com.jinhx.blog.entity.video.vo.VideoVO;

import java.util.List;

/**
 * VideoEsServer
 *
 * @author jinhx
 * @since 2019-04-11
 */
public interface VideoEsServer {

    /**
     * 初始化es视频数据
     *
     * @return 初始化结果
     */
    boolean initVideoList() throws Exception;

    /**
     * 搜索视频
     *
     * @param keyword 关键字
     * @return 搜索结果
     */
    List<VideoVO> searchVideoList(String keyword) throws Exception;

}
