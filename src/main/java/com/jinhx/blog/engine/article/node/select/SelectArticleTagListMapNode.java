package com.jinhx.blog.engine.article.node.select;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * SelectArticleTagListMapNode
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Component
public class SelectArticleTagListMapNode extends ArticleNode<BaseRequestDTO> {

    @Autowired
    private TagMapperService tagMapperService;

    @Autowired
    private TagLinkMapperService tagLinkMapperService;

    @Override
    public boolean isSkip(ArticleQueryContextInfo<BaseRequestDTO> context) {
        return !context.getArticleBuilder().getTagList() || CollectionUtils.isEmpty(context.getArticles());
    }

    @Override
    public void process(ArticleQueryContextInfo<BaseRequestDTO> context) {
        Map<Long, List<Tag>> map = Maps.newHashMap();
        context.getArticles().forEach(item -> {
            List<TagLink> tagLinks = tagLinkMapperService.selectTagLinksByLinkIdAndModule(item.getArticleId(), ModuleTypeConstants.ARTICLE);
            if (CollectionUtils.isNotEmpty(tagLinks)){
                map.put(item.getArticleId(), tagMapperService.listByLinkId(tagLinks));
            }
        });
        context.setArticleTagListMap(map);
    }

    @Override
    public String getProcessorName() {
        return "SelectArticleTagListMapNode";
    }

}
