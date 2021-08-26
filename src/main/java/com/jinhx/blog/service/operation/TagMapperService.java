package com.jinhx.blog.service.operation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.operation.Tag;
import com.jinhx.blog.entity.operation.TagLink;
import com.jinhx.blog.entity.operation.vo.TagVO;
import com.jinhx.blog.mapper.operation.TagMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TagMapperService
 *
 * @author jinhx
 * @since 2019-01-21
 */
@Service
public class TagMapperService extends ServiceImpl<TagMapper, Tag> {

    /**
     * 分页查询标签列表
     *
     * @param page page
     * @param limit limit
     * @param name name
     * @param module module
     * @return 标签列表
     */
    public PageData<Tag> selectPage(Integer page, Integer limit, String name, Integer module) {
        return new PageData<>(baseMapper.selectPage(new QueryPage<Tag>(page, limit).getPage(),
                new LambdaQueryWrapper<Tag>()
                        .like(StringUtils.isNotEmpty(name), Tag::getName, name)
                        .eq(module != null, Tag::getModule, module)));
    }

    /**
     * 根据模块查询标签列表
     *
     * @param module module
     * @return 标签列表
     */
    public List<Tag> selectTagsByModule(Integer module) {
        return baseMapper.selectList(new LambdaQueryWrapper<Tag>().eq(Tag::getModule,module));
    }

    /**
     * 根据tagId查询标签
     *
     * @param tagId tagId
     * @return 标签
     */
    public Tag selectTagById(Long tagId) {
        List<Tag> tags = selectTagsById(Lists.newArrayList(tagId));
        if (CollectionUtils.isEmpty(tags)){
            return null;
        }

        return tags.get(0);
    }

    /**
     * 根据tagId查询标签列表
     *
     * @param tagIds tagIds
     * @return 标签列表
     */
    public List<Tag> selectTagsById(List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)){
            return Lists.newArrayList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<Tag>().in(Tag::getTagId, tagIds));
    }

    /**
     * 新增标签
     *
     * @param tag tag
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertTag(Tag tag) {
        insertTags(Lists.newArrayList(tag));
    }

    /**
     * 批量新增标签
     *
     * @param tags tags
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertTags(List<Tag> tags) {
        if (CollectionUtils.isNotEmpty(tags)){
            if (tags.stream().mapToInt(item -> baseMapper.insert(item)).sum() != tags.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

    /**
     * 根据tagId更新标签
     *
     * @param tag tag
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTagById(Tag tag) {
        updateTagsById(Lists.newArrayList(tag));
    }

    /**
     * 批量根据tagId更新标签
     *
     * @param tags tags
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTagsById(List<Tag> tags) {
        if (CollectionUtils.isNotEmpty(tags)){
            if (tags.stream().mapToInt(item -> baseMapper.updateById(item)).sum() != tags.size()){
                throw new MyException(ResponseEnums.UPDATE_FAILR);
            }
        }
    }

    /**
     * 批量根据tagId删除标签
     *
     * @param tagIds tagIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTagsById(List<Long> tagIds) {
        if (CollectionUtils.isNotEmpty(tagIds)){
            if (baseMapper.deleteBatchIds(tagIds) != tagIds.size()){
                throw new MyException(ResponseEnums.DELETE_FAIL);
            }
        }
    }

    /**
     * 根据关联Id获取列表
     *
     * @param tagLinks tagLinks
     * @return List<Tag>
     */
    public List<Tag> listByLinkId(List<TagLink> tagLinks) {
        if (CollectionUtils.isEmpty(tagLinks)){
            return Collections.emptyList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<Tag>()
                .in(Tag::getTagId, tagLinks.stream().map(TagLink::getTagId).distinct().collect(Collectors.toList())));
    }

    /**
     * 添加所属标签，包含新增标签
     *
     * @param tag tag
     */
    public void saveTagAndNew(Tag tag) {
        baseMapper.insert(tag);
    }

    /********************** portal ********************************/

    /**
     * 根据模块查询标签列表
     *
     * @param module 模块
     * @return 标签列表
     */
    public List<TagVO> selectPortalTagVOsByModule(Integer module) {
        List<TagVO> tagVOs = Lists.newArrayList();
        if(ModuleTypeConstants.ARTICLE.equals(module)){
            tagVOs = baseMapper.selectTagVOsByArticle(module);
            return tagVOs.stream().filter(tagVOListItem -> Integer.parseInt(tagVOListItem.getLinkNum()) > 0).collect(Collectors.toList());
        }
        if(ModuleTypeConstants.VIDEO.equals(module)){
            tagVOs = baseMapper.selectTagVOsByVideo(module);
            return tagVOs.stream().filter(tagVOListItem -> Integer.parseInt(tagVOListItem.getLinkNum()) > 0).collect(Collectors.toList());
        }
        return tagVOs;
    }

}
