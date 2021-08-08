package com.jinhx.blog.entity.article.vo;

import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.operation.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * ArticleVO
 *
 * @author jinhx
 * @since 2018-11-07
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ArticleVO extends Article {

    private static final long serialVersionUID = 5722313456412798232L;

    /**
     * 所属分类，以逗号分隔
     */
    private String categoryListStr;

    /**
     * 所属标签
     */
    private List<Tag> tagList;

    /**
     * 推荐
     */
    private Boolean recommend;

    /**
     * 置顶
     */
    private Boolean top;

    /**
     * 文章作者
     */
    private String author;

}
