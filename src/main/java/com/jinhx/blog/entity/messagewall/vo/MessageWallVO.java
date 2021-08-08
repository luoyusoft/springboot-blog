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

    private static final long serialVersionUID = -4196004574806931431L;

    /**
     * 回复name
     */
    private String replyName;

}
