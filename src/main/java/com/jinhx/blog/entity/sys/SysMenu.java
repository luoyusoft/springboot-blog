package com.jinhx.blog.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * SysMenu
 *
 * @author jinhx
 * @since 2018-10-19
 */
@Data
@ApiModel(value="SysMenu对象", description="系统菜单")
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -8415084251160104448L;

    /**
     * 一级菜单id
     */
    public static final Long ONE_SYS_MENU_ID = 0L;

    /**
     * 一级菜单父菜单id
     */
    public static final Long ONE_PARENT_ID = -1L;

    /**
     * 一级菜单名称
     */
    public static final String ONE_NAME = "一级菜单";

    /**
     * 按钮类型
     */
    public static final Integer TYPE_BUTTON = 1;

    /**
     * 主键
     */
    @ApiModelProperty(value = "系统菜单id主键")
    @TableId(type = IdType.INPUT)
    private Long sysMenuId;

    @ApiModelProperty(value = "父菜单id，一级菜单为0")
    @NotNull(message="父菜单id不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private Long parentId;

    @ApiModelProperty(value = "菜单名称")
    @NotBlank(message="菜单名称不能为空", groups = {InsertGroup.class, UpdateGroup.class})
    private String name;

    @ApiModelProperty(value = "菜单url")
    private String url;

    @ApiModelProperty(value = "授权(多个用逗号分隔，如：user:list,user:create)")
    private String perms;

    @ApiModelProperty(value = "类型（0：目录，1：菜单，2：按钮）")
    @NotNull(message="类型不能为空", groups = {InsertGroup.class})
    private Integer type;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

    @ApiModelProperty(value = "排序")
    @NotNull(message="排序不能为空", groups = {InsertGroup.class})
    private Integer orderNum;

    /**
     * 父菜单名称
     */
    @TableField(exist=false)
    private String parentName;

    /**
     * z-tree属性
     */
    @TableField(exist=false)
    private Boolean open;

    @TableField(exist=false)
    private List<?> list;

}
