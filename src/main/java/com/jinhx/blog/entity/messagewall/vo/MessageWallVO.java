package com.jinhx.blog.entity.messagewall.vo;

import com.jinhx.blog.entity.messagewall.MessageWall;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MessageWallVO
 *
 * @author jinhx
 * @since 2018-11-07
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageWallVO extends MessageWall {

    /**
     * 回复name
     */
    private String replyName;

}
