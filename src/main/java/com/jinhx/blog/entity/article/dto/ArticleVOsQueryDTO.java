package com.jinhx.blog.entity.article.dto;

import com.jinhx.blog.entity.article.ArticleBuilder;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ArticleVOsQueryDTO
 *
 * @author jinhx
 * @since 2021-08-06
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ArticleVOsQueryDTO extends BaseRequestDTO {

    /**
     * 配置需要查询的参数
     */
    private ArticleBuilder articleBuilder;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 页数
     */
    private Integer limit;

    /**
     * 文章标题
     */
    private String title;

}
