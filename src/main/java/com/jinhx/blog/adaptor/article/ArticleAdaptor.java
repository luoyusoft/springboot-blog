package com.jinhx.blog.adaptor.article;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.operation.Category;
import com.jinhx.blog.entity.operation.TagLink;
import com.jinhx.blog.service.operation.*;
import com.jinhx.blog.service.sys.SysUserMapperService;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * ArticleAdaptor
 *
 * @author jinhx
 * @since 2021-07-21
 */
@Component
public class ArticleAdaptor {

    @Autowired
    private TagMapperService tagMapperService;

    @Autowired
    private TagLinkMapperService tagLinkMapperService;

    @Autowired
    private CategoryMapperService categoryMapperService;

    @Autowired
    private RecommendMapperService recommendMapperService;

    @Autowired
    private TopMapperService topMapperService;

    @Autowired
    private SysUserMapperService sysUserMapperService;

    /**
     * 将ArticleVO转换为Article
     *
     * @param articleAdaptorBuilder articleAdaptorBuilder
     * @return Article
     */
    public Article adaptorArticleVOToArticle(ArticleAdaptorBuilder<ArticleVO> articleAdaptorBuilder){
        if(ObjectUtils.isNull(articleAdaptorBuilder) || ObjectUtils.isNull(articleAdaptorBuilder.getData())){
            return null;
        }

        Article article = new Article();
        BeanUtils.copyProperties(articleAdaptorBuilder.getData(), article);
        return article;
    }

    /**
     * 将Article按需转换为ArticleVO
     *
     * @param articleAdaptorBuilder articleAdaptorBuilder
     * @return ArticleVO
     */
    public ArticleVO adaptorArticleToArticleVO(ArticleAdaptorBuilder<Article> articleAdaptorBuilder){
        if(ObjectUtils.isNull(articleAdaptorBuilder) || ObjectUtils.isNull(articleAdaptorBuilder.getData())){
            return null;
        }

        Article article = articleAdaptorBuilder.getData();
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);

        if (articleAdaptorBuilder.getCategoryListStr()){
            List<Category> categorys = categoryMapperService.list(new LambdaQueryWrapper<Category>().eq(Category::getModule, ModuleTypeConstants.ARTICLE));

            if(!CollectionUtils.isEmpty(categorys)){
                articleVO.setCategoryListStr(categoryMapperService.renderCategoryArr(articleVO.getCategoryId(), categorys));
            }
        }

        if (articleAdaptorBuilder.getTagList()){
            List<TagLink> tagLinks = tagLinkMapperService.listTagLinks(articleVO.getId(), ModuleTypeConstants.ARTICLE);
            if (!CollectionUtils.isEmpty(tagLinks)){
                articleVO.setTagList(tagMapperService.listByLinkId(tagLinks));
            }
        }

        if (articleAdaptorBuilder.getRecommend()){
            articleVO.setRecommend(recommendMapperService.selectRecommendByLinkIdAndType(articleVO.getId(), ModuleTypeConstants.ARTICLE) != null);
        }

        if (articleAdaptorBuilder.getTop()){
            articleVO.setTop(topMapperService.isTopByModuleAndLinkId(ModuleTypeConstants.ARTICLE, articleVO.getId()));
        }

        if (articleAdaptorBuilder.getAuthor()){
            articleVO.setAuthor(sysUserMapperService.getNicknameByUserId(articleVO.getCreaterId()));
        }

        return articleVO;
    }

    /**
     * 将Article列表按需转换为ArticleVO列表
     *
     * @param articleAdaptorBuilder articleAdaptorBuilder
     * @return ArticleVO列表
     */
    public List<ArticleVO> adaptorArticlesToArticleVOs(ArticleAdaptorBuilder<List<Article>> articleAdaptorBuilder){
        if(ObjectUtils.isNull(articleAdaptorBuilder) || CollectionUtils.isEmpty(articleAdaptorBuilder.getData())){
            return Collections.emptyList();
        }
        List<ArticleVO> articleVOs = Lists.newArrayList();
        articleAdaptorBuilder.getData().forEach(article -> {
            if (ObjectUtils.isNull(article)){
                return;
            }

            articleVOs.add(adaptorArticleToArticleVO(new ArticleAdaptorBuilder.Builder<Article>()
                    .setCategoryListStr(articleAdaptorBuilder.getCategoryListStr())
                    .setTagList(articleAdaptorBuilder.getTagList())
                    .setRecommend(articleAdaptorBuilder.getRecommend())
                    .setTop(articleAdaptorBuilder.getTop())
                    .setAuthor(articleAdaptorBuilder.getAuthor())
                    .build(article)));
        });

        return articleVOs;
    }

}
