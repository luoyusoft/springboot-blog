package com.jinhx.blog.entity.operation.vo;

import com.jinhx.blog.entity.operation.Tag;
import com.jinhx.blog.entity.operation.Top;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * TopVO
 *
 * @author jinhx
 * @since 2019-02-17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TopVO extends Top {

    private static final long serialVersionUID = 5210408134898228381L;

    private String description;

    private Long readNum;

    private Long watchNum;

    private Long likeNum;

    private String cover;

    private List<Tag> tagList;

    @ApiModelProperty(value = "置顶标题")
    private String title;

}
