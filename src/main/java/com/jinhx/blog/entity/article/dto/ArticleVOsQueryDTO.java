package com.jinhx.blog.entity.article.dto;

import com.jinhx.blog.common.validator.group.QueryGroup;
import com.jinhx.blog.entity.article.ArticleBuilder;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * ArticleVOsQueryDTO
 *
 * @author jinhx
 * @since 2021-08-06
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ArticleVOsQueryDTO extends BaseRequestDTO {

    private static final long serialVersionUID = 2477511590101964323L;

    /**
     * 配置需要查询的参数
     */
    @NotNull(message = "articleBuilder不能为空", groups = {QueryGroup.class})
    private ArticleBuilder articleBuilder;

    /**
     * 页码
     */
    @NotNull(message = "page不能为空", groups = {QueryGroup.class})
    private Integer page;

    /**
     * 页数
     */
    @NotNull(message = "limit不能为空", groups = {QueryGroup.class})
    private Integer limit;

    /**
     * 文章标题
     */
    private String title;

}
