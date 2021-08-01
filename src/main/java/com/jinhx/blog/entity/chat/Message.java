package com.jinhx.blog.entity.chat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Message
 *
 * @author jinhx
 * @since 2019-06-14
 */
@Data
@ApiModel(description = "websocket消息")
public class Message implements Serializable {

    /**
     * 消息推送者
     */
    @ApiModelProperty(value = "消息推送者")
    private User from;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String message;

    /**
     * 消息接收者：
     *      如果是私有（向指定窗口推送），to即为接受者User对象
     *      如果是公共消息（群组聊天），to设为null
     */
    @ApiModelProperty(value = "消息接收者")
    private User to;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    public void setMessage(String message) {
        this.message = message == null ? "" : message.replaceAll("\r\n|\r|\n", "");
    }

}
