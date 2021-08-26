package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.code.kaptcha.Producer;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

/**
 * SysCaptchaService
 *
 * @author jinhx
 * @since 2018-10-08
 */
@Service
public class SysCaptchaService {

    @Autowired
    private Producer producer;

    /**
     * 验证码过期时长5分钟
     */
    public final static long CAPTCHA_EXPIRE = 60 * 5 * 1000;

    /**
     * 获取验证码
     *
     * @param uuid uuid
     * @return 验证码
     */
    public BufferedImage getCaptcha(String uuid) {
        if(StringUtils.isBlank(uuid)){
            throw new MyException(ResponseEnums.NO_UUID);
        }
        // 生成文字验证码
        String code = producer.createText();
        // 存进redis,5分钟后过期
        RedisUtils.set(genRedisKey(uuid), code, CAPTCHA_EXPIRE);
        return producer.createImage(code);
    }

    /**
     * 验证验证码
     *
     * @param uuid uuid
     * @param code 验证码
     * @return 校验结果
     */
    public boolean validate(String uuid, String code) {
        if(StringUtils.isBlank(uuid) || StringUtils.isBlank(code)){
            return false;
        }
        // 从redis中取
        String redisKey = genRedisKey(uuid);
        String captchaCode = RedisUtils.get(redisKey);
        // 删除验证码
        RedisUtils.delete(redisKey);
        return code.equalsIgnoreCase(captchaCode);
    }

    /**
     * 生成redis key
     *
     * @param uuid uuid
     * @return redis key
     */
    private String genRedisKey(String uuid){
        return RedisKeyConstants.MANAGE_SYS_CAPTCHA + uuid;
    }

}
