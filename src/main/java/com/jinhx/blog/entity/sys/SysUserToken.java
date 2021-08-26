package com.jinhx.blog.entity.sys;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * SysUserToken
 *
 * @author jinhx
 * @since 2018-10-19
 */
@Data
@ApiModel(value="SysUserToken对象", description="系统用户Token")
public class SysUserToken implements Serializable {

    private static final long serialVersionUID = 5186217260146502444L;

    private Long sysUserId;

    private String token;

}
