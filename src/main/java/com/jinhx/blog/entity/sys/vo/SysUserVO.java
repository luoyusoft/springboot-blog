package com.jinhx.blog.entity.sys.vo;

import com.jinhx.blog.entity.sys.SysUser;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * SysUserVO
 *
 * @author jinhx
 * @since 2018-10-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysUserVO对象", description="用户管理")
public class SysUserVO extends SysUser {

    private static final long serialVersionUID = 4353527185674315785L;

    private List<Long> roleIdList;

    private String roleNameStr;

}
