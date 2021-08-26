package com.jinhx.blog.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.common.validator.group.UpdateGroup;
import com.jinhx.blog.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * SysRole
 *
 * @author jinhx
 * @since 2018-10-19
 */
@Data
@TableName("sys_role")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysRole对象", description="系统角色")
public class SysRole extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -6539094636430203141L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "系统角色id主键")
    @TableId(type = IdType.INPUT)
    private Long sysRoleId;

    @NotBlank(message = "角色名称不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "备注")
    private String remark;

}
