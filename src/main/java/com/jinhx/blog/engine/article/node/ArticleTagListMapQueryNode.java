package com.jinhx.blog.engine.article.node;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Maps;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.engine.article.ArticleNode;
import com.jinhx.blog.engine.article.ArticleQueryContextInfo;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import com.jinhx.blog.entity.operation.Tag;
import com.jinhx.blog.entity.operation.TagLink;
import com.jinhx.blog.service.operation.TagLinkMapperService;
import com.jinhx.blog.service.operation.TagMapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ArticleTagListMapQueryNode
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Slf4j
@Component
public class ArticleTagListMapQueryNode extends ArticleNode<BaseRequestDTO> {

    @Autowired
    private TagMapperService tagMapperService;

    @Autowired
    private TagLinkMapperService tagLinkMapperService;

    @Override
    public boolean isSkip(ArticleQueryContextInfo<BaseRequestDTO> context) {
        return !context.getArticleBuilder().getTagList() || Objects.isNull(context.getArticleIPage()) || CollectionUtils.isEmpty(context.getArticleIPage().getRecords());
    }

    @Override
    public void process(ArticleQueryContextInfo<BaseRequestDTO> context) {
        Map<Integer, List<Tag>> map = Maps.newHashMap();
        context.getArticleIPage().getRecords().forEach(item -> {
            List<TagLink> tagLinks = tagLinkMapperService.listTagLinks(item.getId(), ModuleTypeConstants.ARTICLE);
            if (CollectionUtils.isNotEmpty(tagLinks)){
                map.put(item.getId(), tagMapperService.listByLinkId(tagLinks));
            }
        });
        context.setArticleTagListMap(map);
    }

    @Override
    public String getProcessorName() {
        return "ArticleTagListMapQueryNode";
    }

}
