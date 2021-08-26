package com.jinhx.blog.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * 七牛云云存储配置类
 *
 * @author jinhx
 * @since 2018-12-17
 */
@Data
@Configuration
public class QiNiuProperties implements Serializable {

    private static final long serialVersionUID = -1397713143362376338L;

    /**
     * qiniuDomain
     */
    @Value("${oss.qiniu.domain}")
    private String qiniuDomain;

    /**
     * 七牛路径前缀
     */
    @Value("${oss.qiniu.prefix}")
    private String qiniuPrefix;

    /**
     * 七牛ACCESS_KEY
     */
    @Value("${oss.qiniu.accessKey}")
    private String qiniuAccessKey;

    /**
     * 七牛SECRET_KEY
     */
    @Value("${oss.qiniu.secretKey}")
    private String qiniuSecretKey;

    /**
     * 七牛空间名
     */
    @Value("${oss.qiniu.bucketName}")
    private String qiniuBucketName;

}
