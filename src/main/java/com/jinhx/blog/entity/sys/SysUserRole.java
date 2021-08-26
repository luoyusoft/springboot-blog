package com.jinhx.blog.entity.sys;

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
 * SysUserRole
 *
 * @author jinhx
 * @since 2018-10-19
 */
@Data
@TableName("sys_user_role")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysUserRole对象", description="系统用户角色")
public class SysUserRole extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -5210668198016777422L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "系统用户角色id主键")
    @TableId(type = IdType.INPUT)
    private Long sysUserRoleId;

    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空", groups = {InsertGroup.class})
    private Long sysUserId;

    @ApiModelProperty(value = "角色id")
    @NotNull(message = "角色id不能为空", groups = {InsertGroup.class})
    private Long sysRoleId;

}
