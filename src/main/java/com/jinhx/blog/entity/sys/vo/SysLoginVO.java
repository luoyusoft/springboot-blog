package com.jinhx.blog.entity.sys.vo;

import lombok.Data;

/**
 * SysLoginVO
 *
 * @author jinhx
 * @since 2018-10-26
 */
@Data
public class SysLoginVO {

    private String username;

    private String password;

    private String captcha;

    private String uuid;

}
