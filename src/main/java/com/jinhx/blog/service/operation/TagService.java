package com.jinhx.blog.service.operation;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.operation.Tag;
import com.jinhx.blog.entity.operation.vo.TagVO;

import java.util.List;

/**
 * TagService
 *
 * @author jinhx
 * @since 2019-01-21
 */
public interface TagService {

    /**
     * 分页查询标签列表
     *
     * @param page page
     * @param limit limit
     * @param name name
     * @param module module
     * @return 标签列表
     */
    PageData<Tag> selectPage(Integer page, Integer limit, String name, Integer module);

    /**
     * 根据模块查询标签列表
     *
     * @param module module
     * @return 标签列表
     */
    List<Tag> selectTagsByModule(Integer module);

    /**
     * 根据tagId查询标签
     *
     * @param tagId tagId
     * @return 标签
     */
    Tag selectTagById(Long tagId);

    /**
     * 新增标签
     *
     * @param tag tag
     */
    void insertTag(Tag tag);

    /**
     * 根据tagId更新标签
     *
     * @param tag tag
     */
    void updateTagById(Tag tag);

    /**
     * 批量根据tagId删除标签
     *
     * @param tagIds tagIds
     */
    void deleteTagsById(List<Long> tagIds);

    /********************** portal ********************************/

    /**
     * 根据模块查询标签列表
     *
     * @param module 模块
     * @return 标签列表
     */
    List<TagVO> selectPortalTagVOsByModule(Integer module);

}
