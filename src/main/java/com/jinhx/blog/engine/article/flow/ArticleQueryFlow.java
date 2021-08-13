package com.jinhx.blog.engine.article.flow;

import com.jinhx.blog.engine.article.ArticleNodeChain;
import com.jinhx.blog.engine.article.node.*;
import com.jinhx.blog.engine.article.node.build.BuildArticleVOIPageNode;
import com.jinhx.blog.engine.article.node.build.BuildArticleVOsNode;
import com.jinhx.blog.engine.article.node.query.ArticleIPageQueryNode;
import com.jinhx.blog.engine.article.node.query.ArticlesQueryNode;
import com.jinhx.blog.engine.article.node.query.HotReadArticlesQueryNode;
import com.jinhx.blog.engine.article.node.query.PortalArticleIPageQueryNode;

/**
 * ArticleQueryFlow
 *
 * @author jinhx
 * @since 2021-08-06
 */
public class ArticleQueryFlow {

    private final static ArticleNodeChain ARTICLEVO_IPAGE_QUERY_FLOW = new ArticleNodeChain();

    private final static ArticleNodeChain PORTAL_ARTICLEVO_IPAGE_QUERY_FLOW = new ArticleNodeChain();

    private final static ArticleNodeChain ARTICLEVOS_QUERY_FLOW = new ArticleNodeChain();

    private final static ArticleNodeChain HOTREAD_ARTICLEVOS_QUERY_FLOW = new ArticleNodeChain();

    static {
        ARTICLEVO_IPAGE_QUERY_FLOW.add(ArticleIPageQueryNode.class);
        ARTICLEVO_IPAGE_QUERY_FLOW.add("group-0", ArticleCategoryListStrMapQueryNode.class);
        ARTICLEVO_IPAGE_QUERY_FLOW.add("group-0", ArticleTagListMapQueryNode.class);
        ARTICLEVO_IPAGE_QUERY_FLOW.add("group-0", ArticleRecommendMapQueryNode.class);
        ARTICLEVO_IPAGE_QUERY_FLOW.add("group-0", ArticleTopMapQueryNode.class);
        ARTICLEVO_IPAGE_QUERY_FLOW.add("group-0", ArticleAuthorMapQueryNode.class);
        ARTICLEVO_IPAGE_QUERY_FLOW.add(BuildArticleVOIPageNode.class);


        PORTAL_ARTICLEVO_IPAGE_QUERY_FLOW.add(PortalArticleIPageQueryNode.class);
        PORTAL_ARTICLEVO_IPAGE_QUERY_FLOW.add("group-0", ArticleCategoryListStrMapQueryNode.class);
        PORTAL_ARTICLEVO_IPAGE_QUERY_FLOW.add("group-0", ArticleTagListMapQueryNode.class);
        PORTAL_ARTICLEVO_IPAGE_QUERY_FLOW.add("group-0", ArticleRecommendMapQueryNode.class);
        PORTAL_ARTICLEVO_IPAGE_QUERY_FLOW.add("group-0", ArticleTopMapQueryNode.class);
        PORTAL_ARTICLEVO_IPAGE_QUERY_FLOW.add("group-0", ArticleAuthorMapQueryNode.class);
        PORTAL_ARTICLEVO_IPAGE_QUERY_FLOW.add(BuildArticleVOIPageNode.class);


        ARTICLEVOS_QUERY_FLOW.add(ArticlesQueryNode.class);
        ARTICLEVOS_QUERY_FLOW.add("group-0", ArticleCategoryListStrMapQueryNode.class);
        ARTICLEVOS_QUERY_FLOW.add("group-0", ArticleTagListMapQueryNode.class);
        ARTICLEVOS_QUERY_FLOW.add("group-0", ArticleRecommendMapQueryNode.class);
        ARTICLEVOS_QUERY_FLOW.add("group-0", ArticleTopMapQueryNode.class);
        ARTICLEVOS_QUERY_FLOW.add("group-0", ArticleAuthorMapQueryNode.class);
        ARTICLEVOS_QUERY_FLOW.add(BuildArticleVOsNode.class);


        HOTREAD_ARTICLEVOS_QUERY_FLOW.add(HotReadArticlesQueryNode.class);
        HOTREAD_ARTICLEVOS_QUERY_FLOW.add("group-0", ArticleCategoryListStrMapQueryNode.class);
        HOTREAD_ARTICLEVOS_QUERY_FLOW.add("group-0", ArticleTagListMapQueryNode.class);
        HOTREAD_ARTICLEVOS_QUERY_FLOW.add("group-0", ArticleRecommendMapQueryNode.class);
        HOTREAD_ARTICLEVOS_QUERY_FLOW.add("group-0", ArticleTopMapQueryNode.class);
        HOTREAD_ARTICLEVOS_QUERY_FLOW.add("group-0", ArticleAuthorMapQueryNode.class);
        HOTREAD_ARTICLEVOS_QUERY_FLOW.add(BuildArticleVOsNode.class);
    }

    public static ArticleNodeChain getArticlevoIpageQueryFlow() {
        return ARTICLEVO_IPAGE_QUERY_FLOW;
    }

    public static ArticleNodeChain getPortalArticlevoIpageQueryFlow() {
        return PORTAL_ARTICLEVO_IPAGE_QUERY_FLOW;
    }

    public static ArticleNodeChain getArticleVOsQueryFlow() {
        return ARTICLEVOS_QUERY_FLOW;
    }

    public static ArticleNodeChain getHotReadArticlevosQueryFlow() {
        return HOTREAD_ARTICLEVOS_QUERY_FLOW;
    }

}
