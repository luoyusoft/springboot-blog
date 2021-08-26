package com.jinhx.blog.entity.sys.vo;

import com.jinhx.blog.entity.sys.SysRole;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * SysRoleVO
 *
 * @author jinhx
 * @since 2021-08-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysRoleVO对象", description="系统角色")
public class SysRoleVO extends SysRole {

    private static final long serialVersionUID = 818996491301001172L;

    private List<Long> menuIdList;

}
