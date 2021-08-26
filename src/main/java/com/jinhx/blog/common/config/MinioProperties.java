package com.jinhx.blog.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * MinioProperties
 *
 * @author jinhx
 * @since 2020-10-07
 */
@Data
@Component
public class MinioProperties {

    /**
     * 对象存储服务的URL
     */
    @Value("${minio.url}")
    private String url;

    /**
     * Access key就像用户ID，可以唯一标识你的账户
     */
    @Value("${minio.accessKey}")
    private String accessKey;

    /**
     * Secret key是你账户的密码
     */
    @Value("${minio.secretKey}")
    private String secretKey;

    /**
     * minioBaseUrl
     */
    @Value("${minio.base.url}")
    private String minioBaseUrl;

    /**
     * minioInitUrl
     */
    @Value("${minio.init.url:https://minio.jinhx.cc}")
    private String minioInitUrl;

}
