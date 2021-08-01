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
@EqualsAndHashCode(callSuper = false)
@Data
public class CategoryVO extends Category {

    @ApiModelProperty(value = "父主键名称")
    private String parentName;

}
