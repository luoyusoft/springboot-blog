package com.jinhx.blog.service.operation.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.common.util.PageUtils;
import com.jinhx.blog.common.util.Query;
import com.jinhx.blog.entity.operation.Tag;
import com.jinhx.blog.entity.operation.TagLink;
import com.jinhx.blog.entity.operation.vo.TagVO;
import com.jinhx.blog.mapper.operation.TagMapper;
import com.jinhx.blog.service.cache.CacheServer;
import com.jinhx.blog.service.operation.TagLinkMapperService;
import com.jinhx.blog.service.operation.TagMapperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
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
public class TagMapperServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagMapperService {

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
    public PageUtils queryPage(Integer page, Integer limit, String name, Integer module) {
        IPage<Tag> tagIPage = baseMapper.selectPage(new Query<Tag>(page, limit).getPage(),
                new LambdaQueryWrapper<Tag>()
                        .like(StringUtils.isNotEmpty(name), Tag::getName, name)
                        .eq(module != null, Tag::getModule, module));

        return new PageUtils(tagIPage);
    }

    /**
     * 根据关联Id获取列表
     *
     * @param tagLinks tagLinks
     * @return List<Tag>
     */
    @Override
    public List<Tag> listByLinkId(List<TagLink> tagLinks) {
        if (CollectionUtils.isEmpty(tagLinks)){
            return Collections.emptyList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<Tag>()
                .in(Tag::getId, tagLinks.stream().map(TagLink::getTagId).distinct().collect(Collectors.toList())));
    }

    /**
     * 添加所属标签，包含新增标签
     *
     * @param tag tag
     */
    @Override
    public void saveTagAndNew(Tag tag) {
        baseMapper.insert(tag);
    }

    /********************** portal ********************************/

}
