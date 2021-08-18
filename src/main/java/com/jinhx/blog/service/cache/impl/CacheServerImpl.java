package com.jinhx.blog.service.cache.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.service.cache.CacheServer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * CacheServerImpl
 *
 * @author jinhx
 * @since 2018-11-07
 */
@Service
public class CacheServerImpl implements CacheServer {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 清除视频相关缓存
     *
     * @param ids 视频id数组
     */
    @Override
    public void cleanVideosCache(Integer[] ids) {
        Set<String> keys = redisTemplate.keys(RedisKeyConstants.VIDEOS + ":" + RedisKeyConstants.REDIS_MATCH_PREFIX);
        keys.addAll(Objects.requireNonNull(redisTemplate.keys(RedisKeyConstants.SEARCHS + ":" + RedisKeyConstants.REDIS_MATCH_PREFIX)));
        keys.addAll(Objects.requireNonNull(redisTemplate.keys(RedisKeyConstants.RECOMMENDS + ":" + ModuleTypeConstants.VIDEO)));

        if (ids != null && ids.length > 0){
            Arrays.stream(ids).forEach(videoId -> {
                keys.add(RedisKeyConstants.VIDEO + ":" + videoId);
            });
        }

        redisTemplate.delete(keys);
        cleanRecommendCache(ModuleTypeConstants.VIDEO);
    }

    /**
     * 清除文章相关缓存
     *
     * @param articleIds 文章id列表
     */
    @Override
    public void cleanArticlesCache(List<Integer> articleIds) {
        Set<String> keys = redisTemplate.keys(RedisKeyConstants.ARTICLES + ":" + RedisKeyConstants.REDIS_MATCH_PREFIX);
        keys.addAll(Objects.requireNonNull(redisTemplate.keys(RedisKeyConstants.TIMELINES + ":" + RedisKeyConstants.REDIS_MATCH_PREFIX)));
        keys.addAll(Objects.requireNonNull(redisTemplate.keys(RedisKeyConstants.SEARCHS + ":" + RedisKeyConstants.REDIS_MATCH_PREFIX)));
        keys.addAll(Objects.requireNonNull(redisTemplate.keys(RedisKeyConstants.RECOMMENDS + ":" + ModuleTypeConstants.ARTICLE)));

        if (CollectionUtils.isNotEmpty(articleIds)){
            articleIds.forEach(articleId -> {
                keys.add(RedisKeyConstants.ARTICLE + ":" + articleId);
            });
        }

        redisTemplate.delete(keys);
        cleanRecommendCache(ModuleTypeConstants.ARTICLE);
    }

    /**
     * 清除相关模块推荐缓存
     *
     * @param module 模块
     */
    @Override
    public void cleanRecommendCache(Integer module) {
        Set<String> keys = redisTemplate.keys(RedisKeyConstants.RECOMMENDS + ":" + module);
        redisTemplate.delete(keys);
    }

    /**
     * 清除推荐所有缓存
     */
    @Override
    public void cleanRecommendAllCache() {
        Set<String> keys = redisTemplate.keys(RedisKeyConstants.RECOMMENDS + ":" + RedisKeyConstants.REDIS_MATCH_PREFIX);
        redisTemplate.delete(keys);
    }

    /**
     * 清除相关模块标签缓存
     *
     * @param module 模块
     */
    @Override
    public void cleanTagsAllCache(Integer module) {
        Set<String> keys = redisTemplate.keys(RedisKeyConstants.TAGS + ":" + module);
        redisTemplate.delete(keys);
    }

    /**
     * 清除分类所有缓存
     */
    @Override
    public void cleanCategorysAllCache() {
        Set<String> keys = redisTemplate.keys(RedisKeyConstants.CATEGORYS + ":" + RedisKeyConstants.REDIS_MATCH_PREFIX);
        redisTemplate.delete(keys);
    }

    /**
     * 清除列表所有缓存
     */
    @Override
    public void cleanListAllCache() {
        Set<String> keys = redisTemplate.keys(RedisKeyConstants.ARTICLES + ":" + RedisKeyConstants.REDIS_MATCH_PREFIX);
        keys.addAll(Objects.requireNonNull(redisTemplate.keys(RedisKeyConstants.VIDEOS + ":" + RedisKeyConstants.REDIS_MATCH_PREFIX)));
        keys.addAll(Objects.requireNonNull(redisTemplate.keys(RedisKeyConstants.SEARCHS + ":" + RedisKeyConstants.REDIS_MATCH_PREFIX)));

        redisTemplate.delete(keys);
    }

    /**
     * 清除所有缓存
     */
    @Override
    public void cleanAllCache() {
        Set<String> keys = redisTemplate.keys(RedisKeyConstants.PROFIX + RedisKeyConstants.REDIS_MATCH_PREFIX);
        redisTemplate.delete(keys);
    }

}
