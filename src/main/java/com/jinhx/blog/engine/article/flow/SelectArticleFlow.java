package com.jinhx.blog.engine.article.flow;

import com.jinhx.blog.engine.article.ArticleNodeChain;
import com.jinhx.blog.engine.article.node.build.BuildArticleVOIPageNode;
import com.jinhx.blog.engine.article.node.build.BuildArticleVOsNode;
import com.jinhx.blog.engine.article.node.select.*;
import com.jinhx.blog.engine.article.node.select.base.SelectArticlePageNode;
import com.jinhx.blog.engine.article.node.select.base.SelectArticlesNode;
import com.jinhx.blog.engine.article.node.select.base.SelectHotReadArticlesNode;
import com.jinhx.blog.engine.article.node.select.base.SelectPortalArticlePageNode;

/**
 * SelectArticleFlow
 *
 * @author jinhx
 * @since 2021-08-06
 */
public class SelectArticleFlow {

    private final static ArticleNodeChain SELECT_ARTICLEVO_PAGE_FLOW = new ArticleNodeChain();

    private final static ArticleNodeChain SELECT_PORTAL_ARTICLEVO_PAGE_FLOW = new ArticleNodeChain();

    private final static ArticleNodeChain SELECT_ARTICLEVOS_FLOW = new ArticleNodeChain();

    private final static ArticleNodeChain SELECT_HOTREAD_ARTICLEVOS_FLOW = new ArticleNodeChain();

    static {
        SELECT_ARTICLEVO_PAGE_FLOW.add(SelectArticlePageNode.class);
        SELECT_ARTICLEVO_PAGE_FLOW.add("group-0", SelectArticleCategoryListStrMapNode.class);
        SELECT_ARTICLEVO_PAGE_FLOW.add("group-0", SelectArticleTagListMapNode.class);
        SELECT_ARTICLEVO_PAGE_FLOW.add("group-0", SelectArticleRecommendMapNode.class);
        SELECT_ARTICLEVO_PAGE_FLOW.add("group-0", SelectArticleTopMapNode.class);
        SELECT_ARTICLEVO_PAGE_FLOW.add("group-0", SelectArticleAuthorMapNode.class);
        SELECT_ARTICLEVO_PAGE_FLOW.add(BuildArticleVOIPageNode.class);


        SELECT_PORTAL_ARTICLEVO_PAGE_FLOW.add(SelectPortalArticlePageNode.class);
        SELECT_PORTAL_ARTICLEVO_PAGE_FLOW.add("group-0", SelectArticleCategoryListStrMapNode.class);
        SELECT_PORTAL_ARTICLEVO_PAGE_FLOW.add("group-0", SelectArticleTagListMapNode.class);
        SELECT_PORTAL_ARTICLEVO_PAGE_FLOW.add("group-0", SelectArticleRecommendMapNode.class);
        SELECT_PORTAL_ARTICLEVO_PAGE_FLOW.add("group-0", SelectArticleTopMapNode.class);
        SELECT_PORTAL_ARTICLEVO_PAGE_FLOW.add("group-0", SelectArticleAuthorMapNode.class);
        SELECT_PORTAL_ARTICLEVO_PAGE_FLOW.add(BuildArticleVOIPageNode.class);


        SELECT_ARTICLEVOS_FLOW.add(SelectArticlesNode.class);
        SELECT_ARTICLEVOS_FLOW.add("group-0", SelectArticleCategoryListStrMapNode.class);
        SELECT_ARTICLEVOS_FLOW.add("group-0", SelectArticleTagListMapNode.class);
        SELECT_ARTICLEVOS_FLOW.add("group-0", SelectArticleRecommendMapNode.class);
        SELECT_ARTICLEVOS_FLOW.add("group-0", SelectArticleTopMapNode.class);
        SELECT_ARTICLEVOS_FLOW.add("group-0", SelectArticleAuthorMapNode.class);
        SELECT_ARTICLEVOS_FLOW.add(BuildArticleVOsNode.class);


        SELECT_HOTREAD_ARTICLEVOS_FLOW.add(SelectHotReadArticlesNode.class);
        SELECT_HOTREAD_ARTICLEVOS_FLOW.add("group-0", SelectArticleCategoryListStrMapNode.class);
        SELECT_HOTREAD_ARTICLEVOS_FLOW.add("group-0", SelectArticleTagListMapNode.class);
        SELECT_HOTREAD_ARTICLEVOS_FLOW.add("group-0", SelectArticleRecommendMapNode.class);
        SELECT_HOTREAD_ARTICLEVOS_FLOW.add("group-0", SelectArticleTopMapNode.class);
        SELECT_HOTREAD_ARTICLEVOS_FLOW.add("group-0", SelectArticleAuthorMapNode.class);
        SELECT_HOTREAD_ARTICLEVOS_FLOW.add(BuildArticleVOsNode.class);
    }

    public static ArticleNodeChain getSelectArticleVOPageFlow() {
        return SELECT_ARTICLEVO_PAGE_FLOW;
    }

    public static ArticleNodeChain getSelectPortalArticleVOPageFlow() {
        return SELECT_PORTAL_ARTICLEVO_PAGE_FLOW;
    }

    public static ArticleNodeChain getSelectArticleVOsFlow() {
        return SELECT_ARTICLEVOS_FLOW;
    }

    public static ArticleNodeChain getSelectHotReadArticleVOsFlow() {
        return SELECT_HOTREAD_ARTICLEVOS_FLOW;
    }

}
