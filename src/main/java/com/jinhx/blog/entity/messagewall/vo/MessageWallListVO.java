package com.jinhx.blog.entity.messagewall.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * HomeMessageWallInfoVO
 *
 * @author jinhx
 * @since 2018-11-07
 */
@Data
public class MessageWallListVO implements Serializable {

    private static final long serialVersionUID = 6370690845772673442L;

    /**
     * 总记录数
     */
    private Integer totalCount;

    /**
     * 是否有更多楼层
     */
    private Boolean haveMoreFloor;

    /**
     * 留言墙列表
     */
    private List<MessageWallVO> messageWallVOList;

}
