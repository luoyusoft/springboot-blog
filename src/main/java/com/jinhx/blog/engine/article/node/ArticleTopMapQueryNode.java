package com.jinhx.blog.engine.article.node;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Maps;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.engine.article.ArticleNode;
import com.jinhx.blog.engine.article.ArticleQueryContextInfo;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import com.jinhx.blog.service.operation.TopMapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * ArticleTopMapQueryNode
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Slf4j
@Component
public class ArticleTopMapQueryNode extends ArticleNode<BaseRequestDTO> {

    @Autowired
    private TopMapperService topMapperService;

    @Override
    public boolean isSkip(ArticleQueryContextInfo<BaseRequestDTO> context) {
        return !context.getArticleBuilder().getTop() || Objects.isNull(context.getArticleIPage()) || CollectionUtils.isEmpty(context.getArticleIPage().getRecords());
    }

    @Override
    public void process(ArticleQueryContextInfo<BaseRequestDTO> context) {
        Map<Integer, Boolean> map = Maps.newHashMap();
        context.getArticleIPage().getRecords().forEach(item -> {
            map.put(item.getId(), topMapperService.isTopByModuleAndLinkId(ModuleTypeConstants.ARTICLE, item.getId()));
        });
        context.setArticleTopMap(map);
    }

    @Override
    public String getProcessorName() {
        return "ArticleTopMapQueryNode";
    }

}
