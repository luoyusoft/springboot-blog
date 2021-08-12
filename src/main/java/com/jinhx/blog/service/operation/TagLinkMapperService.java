package com.jinhx.blog.service.operation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.entity.operation.TagLink;
import com.jinhx.blog.mapper.operation.TagLinkMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TagLinkMapperService
 *
 * @author jinhx
 * @since 2019-01-21
 */
@Service
@Slf4j
public class TagLinkMapperService extends ServiceImpl<TagLinkMapper, TagLink> {

    /**
     * 根据关联Id获取列表
     *
     * @param linkId linkId
     * @param module module
     * @return List<TagLink>
     */
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
    public void deleteTagLink(Integer linkId, Integer module) {
        baseMapper.delete(new LambdaQueryWrapper<TagLink>()
                .eq(linkId != null, TagLink::getLinkId, linkId)
                .eq(module != null, TagLink::getModule, module));
    }

    /********************** portal ********************************/

}
