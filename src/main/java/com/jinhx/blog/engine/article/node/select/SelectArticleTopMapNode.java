package com.jinhx.blog.engine.article.node.select;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Maps;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.engine.article.ArticleNode;
import com.jinhx.blog.engine.article.ArticleQueryContextInfo;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import com.jinhx.blog.service.operation.TopMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * SelectArticleTopMapNode
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Component
public class SelectArticleTopMapNode extends ArticleNode<BaseRequestDTO> {

    @Autowired
    private TopMapperService topMapperService;

    @Override
    public boolean isSkip(ArticleQueryContextInfo<BaseRequestDTO> context) {
        return !context.getArticleBuilder().getTop() || CollectionUtils.isEmpty(context.getArticles());
    }

    @Override
    public void process(ArticleQueryContextInfo<BaseRequestDTO> context) {
        Map<Long, Boolean> map = Maps.newHashMap();
        context.getArticles().forEach(item -> {
            map.put(item.getArticleId(), topMapperService.selectTopCountByOrderNum(ModuleTypeConstants.ARTICLE, item.getArticleId()) > 0);
        });
        context.setArticleTopMap(map);
    }

    @Override
    public String getProcessorName() {
        return "SelectArticleTopMapNode";
    }

}
