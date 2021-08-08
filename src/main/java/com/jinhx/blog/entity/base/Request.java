package com.jinhx.blog.entity.base;

import lombok.Data;

import java.io.Serializable;

/**
 * Request
 *
 * @author jinhx
 * @since 2018-11-07
 */
@Data
public class Request implements Serializable {

    private static final long serialVersionUID = -384864790718140960L;

    private String channel;

    private Object data;

    private String time;

    /**
     * 请求的密文，如果该接口需要加密上送，
     * 则将sdt的密文反序化到data，
     * sdt和action至少有一个为空
     */
    private String sdt;

}
