package com.jinhx.blog.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jinhx.blog.entity.base.BaseEntity;
import com.jinhx.blog.common.validator.group.AddGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * SysParam
 *
 * @author jinhx
 * @since 2018-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_param")
@ApiModel(value="SysParam对象", description="系统参数")
public class SysParam extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -662497005424960310L;

    @ApiModelProperty(value = "参数键")
    @NotNull(message = "参数键不能为空", groups = {AddGroup.class})
    private Integer parKey;

    @ApiModelProperty(value = "参数值")
    @NotBlank(message = "参数值不能为空", groups = {AddGroup.class})
    private String parValue;

    @ApiModelProperty(value = "参数url")
    private String menuUrl;

    @ApiModelProperty(value = "参数类型")
    @NotBlank(message = "参数类型不能为空", groups = {AddGroup.class})
    private String type;

}
