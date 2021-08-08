package com.jinhx.blog.entity.chat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * User
 *
 * @author jinhx
 * @since 2019-06-13
 */
@Data
@ApiModel(description = "websocket用户")
public class User implements Serializable {

    private static final long serialVersionUID = 3871388185877902553L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "浏览器名称")
    private String borderName;

    @ApiModelProperty(value = "浏览器版本")
    private String borderVersion;

    @ApiModelProperty(value = "设备生产厂商")
    private String deviceManufacturer;

    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    @ApiModelProperty(value = "操作系统的版本号")
    private String osVersion;

    @ApiModelProperty(value = "createTime")
    private String createTime;

    public void setName(String name) {
        this.name = name.trim();
    }

}
