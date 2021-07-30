package com.jinhx.blog.service.gitalk;

/**
 * GitalkService
 *
 * @author jinhx
 * @since 2020-11-07
 */
public interface GitalkService {

    /**
     * 初始化gitalk文章数据
     *
     * @return 初始化结果
     */
    boolean initArticleList();

    /**
     * 初始化gitalk视频数据
     *
     * @return 初始化结果
     */
    boolean initVideoList();

}
