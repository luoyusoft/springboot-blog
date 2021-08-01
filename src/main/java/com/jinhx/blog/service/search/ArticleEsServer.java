package com.jinhx.blog.service.search;

import com.jinhx.blog.entity.article.vo.ArticleVO;

import java.util.List;

/**
 * ArticleEsServer
 *
 * @author jinhx
 * @since 2019-04-11
 */
public interface ArticleEsServer {

    /**
     * 初始化es文章数据
     *
     * @return 初始化结果
     */
    boolean initArticleList() throws Exception;

    /**
     * 搜索文章
     *
     * @param keyword 关键字
     * @return 搜索结果
     */
    List<ArticleVO> searchArticleList(String keyword) throws Exception;

}
