package com.jinhx.blog.entity.log.vo;

import lombok.Data;
/**
 * HomeLogInfoVO
 *
 * @author jinhx
 * @since 2019-02-15
 */
@Data
public class HomeLogInfoVO {

    /**
     * 总PV
     */
    private Integer allPV;

    /**
     * 今天PV
     */
    private Integer todayPV;

    /**
     * 总UV
     */
    private Integer allUV;

    /**
     * 今天UV
     */
    private Integer todayUV;

}
