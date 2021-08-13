package com.jinhx.blog.engine.article.node.query;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.SysAdminUtils;
import com.jinhx.blog.engine.article.ArticleNode;
import com.jinhx.blog.engine.article.ArticleQueryContextInfo;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import com.jinhx.blog.service.article.ArticleMapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * ArticlesQueryNode
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Slf4j
@Component
public class ArticlesQueryNode extends ArticleNode<BaseRequestDTO> {

    @Autowired
    private ArticleMapperService articleMapperService;

    @Override
    public boolean isSkip(ArticleQueryContextInfo<BaseRequestDTO> context) {
        if (CollectionUtils.isEmpty(context.getArticleIds())){
            context.setExNextNode(false);
        }
        return false;
    }

    @Override
    public void process(ArticleQueryContextInfo<BaseRequestDTO> context) {
        if (context.getArticleIds().size() == 1){
            Article article = articleMapperService.getArticle(context.getArticleIds().get(0), context.getPublish());
            if (!Objects.isNull(article)){
                if (!article.getCreaterId().equals(SysAdminUtils.getUserId()) && !article.getOpen()){
                    throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "后台查看未公开的文章只能由创建者查看");
                }
                context.setArticles(Lists.newArrayList(article));
            }
        }
        // todo 批量
    }

    @Override
    public String getProcessorName() {
        return "ArticlesQueryNode";
    }

}
