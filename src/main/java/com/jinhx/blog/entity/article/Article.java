package com.jinhx.blog.entity.article;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jinhx.blog.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Article
 *
 * @author jinhx
 * @since 2018-11-07
 */
@Data
@ApiModel(value="Article对象", description="文章")
@EqualsAndHashCode(callSuper = true)
@TableName("article")
public class Article extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -1891100871116824211L;

    /**
     * 发布状态
     */
    public static final Boolean PUBLISH_TRUE = true;

    /**
     * 公开状态
     */
    public static final Boolean OPEN_TRUE = true;

    /**
     * 置顶状态
     */
    public static final Boolean TOP_TRUE = true;

    /**
     * 主键
     */
    @ApiModelProperty(value = "文章id主键")
    @TableId(type = IdType.INPUT)
    private Long articleId;

    @ApiModelProperty(value = "文章标题")
    private String title;

    @ApiModelProperty(value = "文章描述")
    private String description;

    @ApiModelProperty(value = "文章内容")
    private String content;

    @ApiModelProperty(value = "阅读量")
    private Long readNum;

    @ApiModelProperty(value = "点赞量")
    private Long likeNum;

    @ApiModelProperty(value = "封面")
    private String cover;

    @ApiModelProperty(value = "文章展示类别,0:普通，1：大图片，2：无图片")
    private Integer coverType;

    @ApiModelProperty(value = "文章分类类别（存在多级分类，用逗号隔开）")
    private String categoryId;

    @ApiModelProperty(value = "发布状态")
    private Boolean publish;

    @ApiModelProperty(value = "公开状态（0：不公开，1：公开）")
    private Boolean open;

    @ApiModelProperty(value = "格式化后的内容")
    private String contentFormat;

}
