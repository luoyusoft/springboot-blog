package com.jinhx.blog.engine.article.node.query;

import com.jinhx.blog.engine.article.ArticleNode;
import com.jinhx.blog.engine.article.ArticleQueryContextInfo;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import com.jinhx.blog.service.article.ArticleMapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * PortalArticleIPageQueryNode
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Slf4j
@Component
public class PortalArticleIPageQueryNode extends ArticleNode<BaseRequestDTO> {

    @Autowired
    private ArticleMapperService articleMapperService;

    @Override
    public boolean isSkip(ArticleQueryContextInfo<BaseRequestDTO> context) {
        if (Objects.isNull(context.getPage()) || Objects.isNull(context.getLimit()) || Objects.isNull(context.getCategoryId())){
            context.setExNextNode(false);
        }
        return false;
    }

    @Override
    public void process(ArticleQueryContextInfo<BaseRequestDTO> context) {
        context.setArticleIPage(articleMapperService.listArticles(context.getPage(), context.getLimit(), context.getCategoryId(), context.getLatest(), context.getLike(), context.getRead()));
    }

    @Override
    public String getProcessorName() {
        return "PortalArticleIPageQueryNode";
    }

}
