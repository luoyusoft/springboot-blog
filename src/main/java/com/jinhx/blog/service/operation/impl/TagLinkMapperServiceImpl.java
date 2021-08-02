package com.jinhx.blog.service.operation.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.entity.operation.TagLink;
import com.jinhx.blog.mapper.operation.TagLinkMapper;
import com.jinhx.blog.service.cache.CacheServer;
import com.jinhx.blog.service.operation.TagLinkMapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * TagLinkServiceImpl
 *
 * @author jinhx
 * @since 2019-01-21
 */
@Service
@Slf4j
public class TagLinkMapperServiceImpl extends ServiceImpl<TagLinkMapper, TagLink> implements TagLinkMapperService {

    /**
     * 根据关联Id获取列表
     *
     * @param linkId linkId
     * @param module module
     * @return List<TagLink>
     */
    @Override
    public List<TagLink> listTagLinks(Integer linkId, Integer module) {
        return baseMapper.selectList(new LambdaQueryWrapper<TagLink>()
                .eq(TagLink::getLinkId, linkId)
                .eq(TagLink::getModule, module));
    }

    /**
     * 删除tagLink关联
     *
     * @param linkId linkId
     * @param module module
     */
    @Override
    public void deleteTagLink(Integer linkId, Integer module) {
        baseMapper.delete(new LambdaQueryWrapper<TagLink>()
                .eq(linkId != null, TagLink::getLinkId, linkId)
                .eq(module != null, TagLink::getModule, module));
    }

    /********************** portal ********************************/

}
