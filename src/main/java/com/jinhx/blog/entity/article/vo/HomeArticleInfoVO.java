package com.jinhx.blog.entity.article.vo;

import lombok.Data;

/**
 * HomeArticleInfoVO
 *
 * @author jinhx
 * @since 2019-11-07
 */
@Data
public class HomeArticleInfoVO {

    /**
     * 总数量
     */
    private Integer allCount;

    /**
     * 已发布数量
     */
    private Integer publishCount;

}
