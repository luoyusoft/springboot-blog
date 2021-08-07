package com.jinhx.blog.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * NacosConfigService
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Component
@Data
public class NacosConfigService {

    /**
     * 日志输出控制开关
     * true：打开，false：关闭
     */
    @Value("${mdcLog.switch:true}")
    private Boolean mdcLogSwitch;

}
