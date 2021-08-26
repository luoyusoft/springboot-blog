package com.jinhx.blog.service.operation.impl;

import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.threadpool.ThreadPoolEnum;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.operation.Tag;
import com.jinhx.blog.entity.operation.vo.TagVO;
import com.jinhx.blog.service.cache.CacheServer;
import com.jinhx.blog.service.operation.TagLinkMapperService;
import com.jinhx.blog.service.operation.TagMapperService;
import com.jinhx.blog.service.operation.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TagServiceImpl
 *
 * @author jinhx
 * @since 2019-01-21
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapperService tagMapperService;

    @Autowired
    private TagLinkMapperService tagLinkMapperService;

    @Autowired
    private CacheServer cacheServer;

    /**
     * 分页查询标签列表
     *
     * @param page page
     * @param limit limit
     * @param name name
     * @param module module
     * @return 标签列表
     */
    @Override
    public PageData<Tag> selectPage(Integer page, Integer limit, String name, Integer module) {
        return tagMapperService.selectPage(page, limit, name, module);
    }

    /**
     * 根据模块查询标签列表
     *
     * @param module module
     * @return 标签列表
     */
    @Override
    public List<Tag> selectTagsByModule(Integer module) {
        return tagMapperService.selectTagsByModule(module);
    }

    /**
     * 根据tagId查询标签
     *
     * @param tagId tagId
     * @return 标签
     */
    @Override
    public Tag selectTagById(Long tagId) {
        return tagMapperService.selectTagById(tagId);
    }

    /**
     * 新增标签
     *
     * @param tag tag
     */
    @Override
    public void insertTag(Tag tag) {
        tagMapperService.insertTag(tag);
    }

    /**
     * 根据tagId更新标签
     *
     * @param tag tag
     */
    @Override
    public void updateTagById(Tag tag) {
        tagMapperService.updateTagById(tag);
    }

    /**
     * 批量根据tagId删除标签
     *
     * @param tagIds tagIds
     */
    @Override
    public void deleteTagsById(List<Long> tagIds) {
        if (tagLinkMapperService.selectTagLinkCountByTagId(tagIds) > 0){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该标签下有关联，无法删除");
        }
        tagMapperService.deleteTagsById(tagIds);
    }

    /**
     * 清除缓存
     *
     * @param module module
     */
    private void cleanTagsAllCache(Integer module){
        ThreadPoolEnum.COMMON.getThreadPoolExecutor().execute(() ->{
            cacheServer.cleanTagsAllCache(module);
        });
    }

    /********************** portal ********************************/

    /**
     * 根据模块查询标签列表
     *
     * @param module 模块
     * @return 标签列表
     */
    @Cacheable(value = RedisKeyConstants.TAGS, key = "#module")
    @Override
    public List<TagVO> selectPortalTagVOsByModule(Integer module) {
        return tagMapperService.selectPortalTagVOsByModule(module);
    }

}
