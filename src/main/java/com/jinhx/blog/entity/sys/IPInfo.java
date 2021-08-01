package com.jinhx.blog.entity.sys;

import lombok.Data;

import java.io.Serializable;

/**
 * IPInfo
 *
 * @author jinhx
 * @since 2018-10-19
 */
@Data
public class IPInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String country;
    private String countryCode;
    private String region;
    private String regionName;
    private String city;
    private String zip;
    private String lat;
    private String lon;
    private String timezone;
    private String isp;
    private String org;
    private String as;
    private String query;

}
