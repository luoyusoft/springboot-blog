package com.jinhx.blog.entity.article.dto;

import com.jinhx.blog.common.validator.group.SelectGroup;
import com.jinhx.blog.entity.article.ArticleBuilder;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

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
    @NotNull(message = "articleBuilder不能为空", groups = {SelectGroup.class})
    private ArticleBuilder articleBuilder;

    /**
     * 文章id列表
     */
    @NotNull(message = "文章id列表不能为空", groups = {SelectGroup.class})
    private List<Long> articleIds;

    /**
     * 发布状态
     */
    private Boolean publish;

}
