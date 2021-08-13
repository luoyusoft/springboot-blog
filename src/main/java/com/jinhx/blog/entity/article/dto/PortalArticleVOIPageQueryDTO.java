package com.jinhx.blog.entity.article.dto;

import com.jinhx.blog.common.validator.group.QueryGroup;
import com.jinhx.blog.entity.article.ArticleBuilder;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * PortalArticleVOIPageQueryDTO
 *
 * @author jinhx
 * @since 2021-08-06
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PortalArticleVOIPageQueryDTO extends BaseRequestDTO {

    private static final long serialVersionUID = 52653230435878380L;

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
     * 分类
     */
    @NotNull(message = "分类不能为空", groups = {QueryGroup.class})
    private Integer categoryId;

    /**
     * 时间排序
     */
    private Boolean latest;

    /**
     * 点赞量排序
     */
    private Boolean like;

    /**
     * 阅读量排序
     */
    private Boolean read;

}
