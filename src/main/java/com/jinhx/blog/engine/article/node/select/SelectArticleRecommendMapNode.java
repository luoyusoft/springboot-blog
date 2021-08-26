package com.jinhx.blog.engine.article.node.select;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Maps;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.engine.article.ArticleNode;
import com.jinhx.blog.engine.article.ArticleQueryContextInfo;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import com.jinhx.blog.service.operation.RecommendMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * SelectArticleRecommendMapNode
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Component
public class SelectArticleRecommendMapNode extends ArticleNode<BaseRequestDTO> {

    @Autowired
    private RecommendMapperService recommendMapperService;

    @Override
    public boolean isSkip(ArticleQueryContextInfo<BaseRequestDTO> context) {
        return !context.getArticleBuilder().getRecommend() || CollectionUtils.isEmpty(context.getArticles());
    }

    @Override
    public void process(ArticleQueryContextInfo<BaseRequestDTO> context) {
        Map<Long, Boolean> map = Maps.newHashMap();
        context.getArticles().forEach(item -> {
            map.put(item.getArticleId(), Objects.nonNull(recommendMapperService.selectRecommendByLinkIdAndModule(item.getArticleId(), ModuleTypeConstants.ARTICLE)));
        });
        context.setArticleRecommendMap(map);
    }

    @Override
    public String getProcessorName() {
        return "SelectArticleRecommendMapNode";
    }

}
