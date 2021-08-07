package com.jinhx.blog.engine.article.flow;

import com.jinhx.blog.engine.article.ArticleNodeChain;
import com.jinhx.blog.engine.article.node.*;

/**
 * ArticleVOsQueryFlow
 *
 * @author jinhx
 * @since 2021-08-06
 */
public class ArticleVOsQueryFlow {

    private final static ArticleNodeChain ARTICLEVOS_QUERY_FLOW = new ArticleNodeChain();

    static {
        ARTICLEVOS_QUERY_FLOW.add("group-0", ArticleIPageQueryNode.class);
        ARTICLEVOS_QUERY_FLOW.add("group-1", ArticleCategoryListStrMapQueryNode.class);
        ARTICLEVOS_QUERY_FLOW.add("group-1", ArticleTagListMapQueryNode.class);
        ARTICLEVOS_QUERY_FLOW.add("group-1", ArticleRecommendMapQueryNode.class);
        ARTICLEVOS_QUERY_FLOW.add("group-1", ArticleTopMapQueryNode.class);
        ARTICLEVOS_QUERY_FLOW.add("group-1", ArticleAuthorMapQueryNode.class);
        ARTICLEVOS_QUERY_FLOW.add(BuildArticleVOIPageNode.class);
    }

    public static ArticleNodeChain getArticleVOsQueryFlow() {
        return ARTICLEVOS_QUERY_FLOW;
    }

}
