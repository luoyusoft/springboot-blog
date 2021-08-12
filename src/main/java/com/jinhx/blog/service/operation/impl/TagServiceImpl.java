package com.jinhx.blog.service.operation.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.common.threadpool.ThreadPoolEnum;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.operation.Tag;
import com.jinhx.blog.entity.operation.TagLink;
import com.jinhx.blog.entity.operation.vo.TagVO;
import com.jinhx.blog.mapper.operation.TagMapper;
import com.jinhx.blog.service.cache.CacheServer;
import com.jinhx.blog.service.operation.TagLinkMapperService;
import com.jinhx.blog.service.operation.TagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TagServiceImpl
 *
 * @author jinhx
 * @since 2019-01-21
 */
@Service
@Slf4j
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private TagLinkMapperService tagLinkMapperService;

    @Autowired
    private CacheServer cacheServer;

    /**
     * 分页查询
     *
     * @param page page
     * @param limit limit
     * @param name name
     * @param module module
     * @return PageUtils
     */
    @Override
    public PageData queryPage(Integer page, Integer limit, String name, Integer module) {
        IPage<Tag> tagIPage = baseMapper.selectPage(new QueryPage<Tag>(page, limit).getPage(),
                new LambdaQueryWrapper<Tag>()
                        .like(StringUtils.isNotEmpty(name), Tag::getName, name)
                        .eq(module != null, Tag::getModule, module));

        return new PageData(tagIPage);
    }

    /**
     * 根据关联Id获取列表
     *
     * @param linkId linkId
     * @param module module
     * @return List<Tag>
     */
    @Override
    public List<Tag> listByLinkId(Integer linkId, Integer module) {
        List<TagLink> tagLinks = tagLinkMapperService.listTagLinks(linkId, module);
        if (CollectionUtils.isEmpty(tagLinks)){
            return Collections.emptyList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<Tag>()
                .in(Tag::getId, tagLinks.stream().map(TagLink::getTagId).distinct().collect(Collectors.toList())));
    }

    /**
     * 添加所属标签，包含新增标签
     *
     * @param tagList tagList
     * @param linkId linkId
     * @param module module
     */
    @Override
    public void saveTagAndNew(List<Tag> tagList, Integer linkId, Integer module) {
        Optional.ofNullable(tagList)
                .ifPresent(tagListValue -> tagListValue.forEach(tag -> {
                    if (Objects.isNull(tag.getId())) {
                        baseMapper.insert(tag);
                    }
                    TagLink tagLink = new TagLink();
                    tagLink.setLinkId(linkId);
                    tagLink.setTagId(tag.getId());
                    tagLink.setModule(module);
                    tagLinkMapperService.save(tagLink);
                }));

        cleanTagsAllCache(module);
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
     * 获取标签列表
     *
     * @param module 模块
     * @return 标签列表
     */
    @Cacheable(value = RedisKeyConstants.TAGS, key = "#module")
    @Override
    public List<TagVO> listTags(Integer module) {
        List<TagVO> tagVOs = Lists.newArrayList();
        if(ModuleTypeConstants.ARTICLE.equals(module)){
            tagVOs = baseMapper.listTagVOsByArticle(module);
            return tagVOs.stream().filter(tagVOListItem -> Integer.parseInt(tagVOListItem.getLinkNum()) > 0).collect(Collectors.toList());
        }
        if(ModuleTypeConstants.VIDEO.equals(module)){
            tagVOs = baseMapper.listTagVOsByVideo(module);
            return tagVOs.stream().filter(tagVOListItem -> Integer.parseInt(tagVOListItem.getLinkNum()) > 0).collect(Collectors.toList());
        }
        return tagVOs;
    }

}
