package com.jinhx.blog.engine.article.node.select.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.jinhx.blog.engine.article.ArticleNode;
import com.jinhx.blog.engine.article.ArticleQueryContextInfo;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import com.jinhx.blog.service.article.ArticleMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * SelectArticlePageNode
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Component
public class SelectArticlePageNode extends ArticleNode<BaseRequestDTO> {

    @Autowired
    private ArticleMapperService articleMapperService;

    @Override
    public boolean isSkip(ArticleQueryContextInfo<BaseRequestDTO> context) {
        if (Objects.isNull(context.getPage()) || Objects.isNull(context.getLimit())){
            context.setExNextNode(false);
        }
        return false;
    }

    @Override
    public void process(ArticleQueryContextInfo<BaseRequestDTO> context) {
        IPage<Article> articleIPage = articleMapperService.selectPage(context.getPage(), context.getLimit(), context.getTitle());
        if (Objects.nonNull(articleIPage) && CollectionUtils.isNotEmpty(articleIPage.getRecords())){
            context.setArticles(articleIPage.getRecords());
        }
        context.setArticleIPage(articleIPage);
    }

    @Override
    public String getProcessorName() {
        return "SelectArticlePageNode";
    }

}
