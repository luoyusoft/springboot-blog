package com.jinhx.blog.engine.article.node;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Maps;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.engine.article.ArticleNode;
import com.jinhx.blog.engine.article.ArticleQueryContextInfo;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import com.jinhx.blog.entity.operation.Category;
import com.jinhx.blog.service.operation.CategoryMapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * ArticleCategoryListStrMapQueryNode
 *
 * @author jinhx
 * @since 2021-07-21
 */
@Slf4j
@Component
public class ArticleCategoryListStrMapQueryNode extends ArticleNode<BaseRequestDTO> {

    @Autowired
    private CategoryMapperService categoryMapperService;

    @Override
    public boolean isSkip(ArticleQueryContextInfo<BaseRequestDTO> context) {
        return !context.getArticleBuilder().getCategoryListStr() || CollectionUtils.isEmpty(context.getArticles());
    }

    @Override
    public void process(ArticleQueryContextInfo<BaseRequestDTO> context) {
        List<Category> categorys = categoryMapperService.list(new LambdaQueryWrapper<Category>().eq(Category::getModule, ModuleTypeConstants.ARTICLE));

        Map<Integer, String> map = Maps.newHashMap();
        if(CollectionUtils.isNotEmpty(categorys)){
            context.getArticles().forEach(item -> {
                map.put(item.getId(), categoryMapperService.renderCategoryArr(item.getCategoryId(), categorys));
            });
        }
        context.setArticleCategoryListStrMap(map);
    }

    @Override
    public String getProcessorName() {
        return "ArticleCategoryListStrMapQueryNode";
    }

}
