package com.jinhx.blog.service.operation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.operation.TagLink;
import com.jinhx.blog.mapper.operation.TagLinkMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TagLinkMapperService
 *
 * @author jinhx
 * @since 2019-01-21
 */
@Service
public class TagLinkMapperService extends ServiceImpl<TagLinkMapper, TagLink> {

    /**
     * 根据tagId查询标签链接数量
     *
     * @param tagIds tagIds
     * @return 标签链接数量
     */
    public int selectTagLinkCountByTagId(List<Long> tagIds) {
        return baseMapper.selectCount(new LambdaQueryWrapper<TagLink>().in(TagLink::getTagId, tagIds));
    }

    /**
     * 根据linkId，模块查询标签链接列表
     *
     * @param linkId linkId
     * @param module module
     * @return 标签链接列表
     */
    public List<TagLink> selectTagLinksByLinkIdAndModule(Long linkId, Integer module) {
        return baseMapper.selectList(new LambdaQueryWrapper<TagLink>()
                .eq(TagLink::getLinkId, linkId)
                .eq(TagLink::getModule, module));
    }

    /**
     * 根据linkId，模块删除标签链接
     *
     * @param linkId linkId
     * @param module module
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTagLinksByLinkIdAndModule(Long linkId, Integer module) {
        baseMapper.delete(new LambdaQueryWrapper<TagLink>()
                .eq(TagLink::getLinkId, linkId)
                .eq(TagLink::getModule, module));
    }

    /**
     * 新增标签链接
     *
     * @param tagLink 标签链接
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertTagLink(TagLink tagLink) {
        insertTagLinks(Lists.newArrayList(tagLink));
    }

    /**
     * 批量新增标签链接
     *
     * @param tagLinks 标签链接列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertTagLinks(List<TagLink> tagLinks) {
        if (CollectionUtils.isNotEmpty(tagLinks)){
            if (tagLinks.stream().mapToInt(item -> baseMapper.insert(item)).sum() != tagLinks.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

    /********************** portal ********************************/

}
