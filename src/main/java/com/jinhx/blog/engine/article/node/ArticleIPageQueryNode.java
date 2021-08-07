package com.jinhx.blog.engine.article.node;

import com.jinhx.blog.engine.article.ArticleNode;
import com.jinhx.blog.engine.article.ArticleQueryContextInfo;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import com.jinhx.blog.service.article.ArticleMapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * ArticleIPageQueryNode
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Slf4j
@Component
public class ArticleIPageQueryNode extends ArticleNode<BaseRequestDTO> {

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
        context.setArticleIPage(articleMapperService.queryPage(context.getPage(), context.getLimit(), context.getTitle()));
    }

    @Override
    public String getProcessorName() {
        return "ArticleIPageQueryNode";
    }

}
