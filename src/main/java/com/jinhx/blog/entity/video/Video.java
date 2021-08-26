package com.jinhx.blog.entity.video;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.jinhx.blog.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Video
 *
 * @author jinhx
 * @since 2018-11-22
 */
@Data
@ApiModel(value="Video对象", description="视频")
@EqualsAndHashCode(callSuper = true)
@TableName("video")
public class Video extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -840045152275397777L;

    /**
     * 发布状态
     */
    public static final Boolean PUBLISH_TRUE = true;

    /**
     * 主键
     */
    @ApiModelProperty(value = "视频id主键")
    @TableId(type = IdType.INPUT)
    private Long videoId;

    @ApiModelProperty(value = "视频标题")
    private String title;

    @ApiModelProperty(value = "视频又名")
    private String alternateName;

    @ApiModelProperty(value = "封面")
    private String cover;

    @ApiModelProperty(value = "视频地址")
    private String videoUrl;

    @ApiModelProperty(value = "视频分类类别（存在多级分类，用逗号隔开）")
    private String categoryId;

    @ApiModelProperty(value = "制片国家/地区")
    private String productionRegion;

    @ApiModelProperty(value = "导演")
    private String director;

    @ApiModelProperty(value = "上映日期")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseTime;

    @ApiModelProperty(value = "片长（格式：HH:mm:ss）")
    private String duration;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "主演")
    private String toStar;

    @ApiModelProperty(value = "评分")
    private String score;

    @ApiModelProperty(value = "编剧")
    private String screenwriter;

    @ApiModelProperty(value = "剧情简介")
    private String synopsis;

    @ApiModelProperty(value = "发布状态")
    private Boolean publish;

    @ApiModelProperty(value = "观看量")
    private Long watchNum;

    @ApiModelProperty(value = "点赞量")
    private Long likeNum;

}
