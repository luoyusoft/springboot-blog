package com.jinhx.blog.service.operation;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.common.util.PageUtils;
import com.jinhx.blog.entity.operation.Tag;
import com.jinhx.blog.entity.operation.TagLink;
import com.jinhx.blog.entity.operation.vo.TagVO;

import java.util.List;

/**
 * TagService
 *
 * @author jinhx
 * @since 2019-01-21
 */
public interface TagMapperService extends IService<Tag> {

    /**
     * 分页查询
     *
     * @param page page
     * @param limit limit
     * @param name name
     * @param module module
     * @return PageUtils
     */
    PageUtils queryPage(Integer page, Integer limit, String name, Integer module);

    /**
     * 根据关联Id获取列表
     *
     * @param tagLinks tagLinks
     * @return List<Tag>
     */
    List<Tag> listByLinkId(List<TagLink> tagLinks);

    /**
     * 添加所属标签，包含新增标签
     *
     * @param tag tag
     */
    void saveTagAndNew(Tag tag);

    /********************** portal ********************************/

}
