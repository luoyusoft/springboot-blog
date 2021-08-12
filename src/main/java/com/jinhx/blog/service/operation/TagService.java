package com.jinhx.blog.service.operation;

import com.baomidou.mybatisplus.extension.service.IService;
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
public interface TagService extends IService<Tag> {

    /**
     * 分页查询
     *
     * @param page page
     * @param limit limit
     * @param name name
     * @param module module
     * @return PageUtils
     */
    PageData queryPage(Integer page, Integer limit, String name, Integer module);

    /**
     * 根据关联Id获取列表
     *
     * @param linkId linkId
     * @param module module
     * @return List<Tag>
     */
    List<Tag> listByLinkId(Integer linkId, Integer module);

    /**
     * 添加所属标签，包含新增标签
     *
     * @param tagList tagList
     * @param linkId linkId
     * @param module module
     */
    void saveTagAndNew(List<Tag> tagList, Integer linkId, Integer module);

    /********************** portal ********************************/

    /**
     * 获取标签列表
     *
     * @param module 模块
     * @return 标签列表
     */
    List<TagVO> listTags(Integer module);

}
