package com.jinhx.blog.service.sys;


import java.awt.image.BufferedImage;

/**
 * SysCaptchaService
 *
 * @author jinhx
 * @since 2018-10-08
 */
public interface SysCaptchaService {

    /**
     * 获取验证码
     *
     * @param uuid uuid
     * @return 验证码
     */
    BufferedImage getCaptcha(String uuid);

    /**
     * 验证验证码
     *
     * @param uuid uuid
     * @param code 验证码
     * @return 校验结果
     */
    boolean validate(String uuid, String code);

}
