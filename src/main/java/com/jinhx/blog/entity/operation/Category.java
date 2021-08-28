package com.jinhx.blog.entity.operation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Category
 *
 * @author jinhx
 * @since 2018-12-17
 */
@Data
@ApiModel(value="Category对象", description="类别")
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -8525248598661244699L;

    @ApiModelProperty(value = "类别id主键")
    @TableId(type = IdType.INPUT)
    private Long categoryId;

    @ApiModelProperty(value = "名称")
    @NotBlank(message = "名称不能为空", groups = {InsertGroup.class})
    private String name;

    @ApiModelProperty(value = "模块")
    @NotNull(message = "模块不能为空", groups = {InsertGroup.class})
    private Integer module;

    @ApiModelProperty(value = "级别")
    @NotNull(message = "级别不能为空", groups = {InsertGroup.class})
    @TableField(value = "`rank`")
    private Integer rank;

    @ApiModelProperty(value = "父类别id")
    @NotNull(message = "父类别id不能为空", groups = {InsertGroup.class})
    private Long parentId;

}
