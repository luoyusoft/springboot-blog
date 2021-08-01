package com.jinhx.blog.entity.messagewall.vo;

import lombok.Data;

/**
 * HomeMessageWallInfoVO
 *
 * @author jinhx
 * @since 2019-01-07
 */
@Data
public class HomeMessageWallInfoVO {

    /**
     * 总留言量
     */
    private Integer allCount;

    /**
     * 今天留言量
     */
    private Integer todayCount;

    /**
     * 最大楼层
     */
    private Integer maxFloorNum;

}
