package com.jinhx.blog.entity.operation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * TagLink
 *
 * @author jinhx
 * @since 2019-01-07
 */
@Data
@ApiModel(value="TagLink对象", description="标签链接")
@EqualsAndHashCode(callSuper = true)
@TableName("tag_link")
public class TagLink extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1347335185884378936L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "标签链接id主键")
    @TableId(type = IdType.INPUT)
    private Long tagLinkId;

    @ApiModelProperty(value = "标签链接id")
    @NotNull(message="标签链接id不能为空", groups = {InsertGroup.class})
    private Long linkId;

    @ApiModelProperty(value = "标签id")
    @NotNull(message="标签id不能为空", groups = {InsertGroup.class})
    private Long tagId;

    @ApiModelProperty(value = "标签所属模块：0文章，1阅读")
    @NotNull(message="所属模块不能为空", groups = {InsertGroup.class})
    private Integer module;

}
