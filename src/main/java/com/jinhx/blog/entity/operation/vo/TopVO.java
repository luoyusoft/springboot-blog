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
@EqualsAndHashCode(callSuper = false)
@Data
public class TopVO extends Top {

    private String description;

    private Long readNum;

    private Long watchNum;

    private Long likeNum;

    private String cover;

    private List<Tag> tagList;

    @ApiModelProperty(value = "置顶标题")
    private String title;

}
