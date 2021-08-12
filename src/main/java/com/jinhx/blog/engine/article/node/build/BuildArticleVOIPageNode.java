package com.jinhx.blog.engine.article.node.build;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.jinhx.blog.engine.article.ArticleNode;
import com.jinhx.blog.engine.article.ArticleQueryContextInfo;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.ArticleBuilder;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * BuildArticleVOIPageNode
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Slf4j
@Component
public class BuildArticleVOIPageNode extends ArticleNode<BaseRequestDTO> {

    @Override
    public boolean isSkip(ArticleQueryContextInfo<BaseRequestDTO> context) {
        return Objects.isNull(context.getArticleIPage()) || CollectionUtils.isEmpty(context.getArticleIPage().getRecords());
    }

    @Override
    public void process(ArticleQueryContextInfo<BaseRequestDTO> context) {
        ArticleBuilder articleBuilder = context.getArticleBuilder();
        IPage<Article> articleIPage = context.getArticleIPage();

        IPage<ArticleVO> articleVOIPage = new Page<>();
        BeanUtils.copyProperties(articleIPage, articleVOIPage);

        List<ArticleVO> articleVOs = Lists.newArrayList();
        articleIPage.getRecords().forEach(item -> {
            ArticleVO articleVO = new ArticleVO();
            BeanUtils.copyProperties(item, articleVO);

            if (articleBuilder.getCategoryListStr()){
                articleVO.setCategoryListStr(context.getArticleCategoryListStrMap().get(articleVO.getId()));
            }

            if (articleBuilder.getTagList()){
                articleVO.setTagList(context.getArticleTagListMap().get(articleVO.getId()));
            }

            if (articleBuilder.getRecommend()){
                articleVO.setRecommend(context.getArticleRecommendMap().get(articleVO.getId()));
            }

            if (articleBuilder.getTop()){
                articleVO.setTop(context.getArticleTopMap().get(articleVO.getId()));
            }

            if (articleBuilder.getAuthor()){
                articleVO.setAuthor(context.getArticleAuthorMap().get(articleVO.getId()));
            }

            articleVOs.add(articleVO);
        });

        articleVOIPage.setRecords(articleVOs);
        context.setArticleVOIPage(articleVOIPage.setRecords(articleVOs));
    }

    @Override
    public String getProcessorName() {
        return "BuildArticleVOIPageNode";
    }

}
