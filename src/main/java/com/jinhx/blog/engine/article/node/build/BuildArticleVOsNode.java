package com.jinhx.blog.engine.article.node.build;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.jinhx.blog.engine.article.ArticleNode;
import com.jinhx.blog.engine.article.ArticleQueryContextInfo;
import com.jinhx.blog.entity.article.ArticleBuilder;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * BuildArticleVOsNode
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Slf4j
@Component
public class BuildArticleVOsNode extends ArticleNode<BaseRequestDTO> {

    @Override
    public boolean isSkip(ArticleQueryContextInfo<BaseRequestDTO> context) {
        return CollectionUtils.isEmpty(context.getArticles());
    }

    @Override
    public void process(ArticleQueryContextInfo<BaseRequestDTO> context) {
        ArticleBuilder articleBuilder = context.getArticleBuilder();

        List<ArticleVO> articleVOs = Lists.newArrayList();
        context.getArticles().forEach(item -> {
            ArticleVO articleVO = new ArticleVO();
            BeanUtils.copyProperties(item, articleVO);

            if (context.getArticles().size() > 1){
                articleVO.listDataDesensitization();
            }

            if (articleBuilder.getCategoryListStr()){
                articleVO.setCategoryListStr(context.getArticleCategoryListStrMap().get(articleVO.getArticleId()));
            }

            if (articleBuilder.getTagList()){
                articleVO.setTagList(context.getArticleTagListMap().get(articleVO.getArticleId()));
            }

            if (articleBuilder.getRecommend()){
                articleVO.setRecommend(context.getArticleRecommendMap().get(articleVO.getArticleId()));
            }

            if (articleBuilder.getTop()){
                articleVO.setTop(context.getArticleTopMap().get(articleVO.getArticleId()));
            }

            if (articleBuilder.getAuthor()){
                articleVO.setAuthor(context.getArticleAuthorMap().get(articleVO.getArticleId()));
            }

            articleVOs.add(articleVO);
        });

        context.setArticleVOs(articleVOs);
    }

    @Override
    public String getProcessorName() {
        return "BuildArticleVOsNode";
    }

}
