package com.jinhx.blog.engine.article.node.select.base;

import com.jinhx.blog.engine.article.ArticleNode;
import com.jinhx.blog.engine.article.ArticleQueryContextInfo;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import com.jinhx.blog.service.article.ArticleMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * SelectHotReadArticlesNode
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Component
public class SelectHotReadArticlesNode extends ArticleNode<BaseRequestDTO> {

    @Autowired
    private ArticleMapperService articleMapperService;

    @Override
    public boolean isSkip(ArticleQueryContextInfo<BaseRequestDTO> context) {
        return false;
    }

    @Override
    public void process(ArticleQueryContextInfo<BaseRequestDTO> context) {
        context.setArticles(articleMapperService.selectHotReadArticles());
    }

    @Override
    public String getProcessorName() {
        return "SelectHotReadArticlesNode";
    }

}
