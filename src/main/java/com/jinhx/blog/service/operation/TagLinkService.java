package com.jinhx.blog.service.operation;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.entity.operation.TagLink;

import java.util.List;

/**
 * TagLinkService
 *
 * @author jinhx
 * @since 2019-01-21
 */
public interface TagLinkService extends IService<TagLink> {

    /**
     * 根据关联Id获取列表
     *
     * @param linkId linkId
     * @param module module
     * @return List<TagLink>
     */
    List<TagLink> listTagLinks(Integer linkId, Integer module);

    /**
     * 删除tagLink关联
     *
     * @param linkId linkId
     * @param module module
     */
    void deleteTagLink(Integer linkId, Integer module);

    /********************** portal ********************************/

}
