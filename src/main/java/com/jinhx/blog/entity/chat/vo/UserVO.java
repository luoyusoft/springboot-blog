package com.jinhx.blog.entity.chat.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * UserVO
 *
 * @author jinhx
 * @since 2019-06-13
 */
@Data
@ApiModel(description = "websocket用户VO")
public class UserVO implements Serializable {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "avatar")
    private String avatar;

    @ApiModelProperty(value = "createTime")
    private String createTime;

    public void setName(String name) {
        this.name = name.trim();
    }

}
