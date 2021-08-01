package com.jinhx.blog.entity.operation.vo;

import com.jinhx.blog.entity.operation.Recommend;
import com.jinhx.blog.entity.operation.Tag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * RecommendVO
 *
 * @author jinhx
 * @since 2019-02-17
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class RecommendVO extends Recommend {

    private String description;

    private Long readNum;

    private Long watchNum;

    private Long likeNum;

    private String cover;

    private List<Tag> tagList;

    @ApiModelProperty(value = "推荐标题")
    private String title;

}
