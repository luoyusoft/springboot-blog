package com.jinhx.blog.entity.operation.vo;

import com.jinhx.blog.entity.operation.Category;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CategoryVO
 *
 * @author jinhx
 * @since 2018-12-17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CategoryVO extends Category {

    private static final long serialVersionUID = 215115213455726607L;

    @ApiModelProperty(value = "父主键名称")
    private String parentName;

}
