package com.jinhx.blog.entity.operation;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jinhx.blog.common.validator.group.AddGroup;
import com.jinhx.blog.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Tag
 *
 * @author jinhx
 * @since 2018-11-07
 */
@Data
@ApiModel(value="Tag对象", description="标签")
@EqualsAndHashCode(callSuper = false)
@TableName("tag")
public class Tag extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "标签名称")
    @NotBlank(message="标签名称不能为空", groups = {AddGroup.class})
    private String name;

    @ApiModelProperty(value = "标签所属模块：0文章，1视频")
    @NotNull(message="标签所属模块不能为空", groups = {AddGroup.class})
    private Integer module;

}
