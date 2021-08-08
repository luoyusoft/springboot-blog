package com.jinhx.blog.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jinhx.blog.common.validator.group.AddGroup;
import com.jinhx.blog.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * SysRoleMenu
 *
 * @author jinhx
 * @since 2018-10-19
 */
@Data
@TableName("sys_role_menu")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysRoleMenu对象", description="角色与菜单对应关系")
public class SysRoleMenu extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 8947211881660774714L;

    @ApiModelProperty(value = "角色id")
    @NotNull(message = "角色id不能为空", groups = {AddGroup.class})
    private Integer roleId;

    @ApiModelProperty(value = "菜单id")
    @NotNull(message = "菜单id不能为空", groups = {AddGroup.class})
    private Integer menuId;

}
