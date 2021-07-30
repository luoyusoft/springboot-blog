package com.jinhx.blog.service.cache;

/**
 * CacheServer
 *
 * @author jinhx
 * @since 2018-11-07
 */
public interface CacheServer {

    /**
     * 清除视频相关缓存
     *
     * @param ids 视频id数组
     */
    void cleanVideosCache(Integer[] ids);

    /**
     * 清除文章相关缓存
     *
     * @param ids 文章id数组
     */
    void cleanArticlesCache(Integer[] ids);

    /**
     * 清除相关模块推荐缓存
     *
     * @param module 模块
     */
    void cleanRecommendCache(Integer module);

    /**
     * 清除推荐所有缓存
     */
    void cleanRecommendAllCache();

    /**
     * 清除相关模块标签缓存
     *
     * @param module 模块
     */
    void cleanTagsAllCache(Integer module);

    /**
     * 清除分类所有缓存
     */
    void cleanCategorysAllCache();

    /**
     * 清除列表所有缓存
     */
    void cleanListAllCache();

    /**
     * 清除所有缓存
     */
    void cleanAllCache();

}
