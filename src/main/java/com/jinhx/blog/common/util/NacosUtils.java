package com.jinhx.blog.common.util;

import com.jinhx.blog.common.config.NacosConfigService;

/**
 * NacosUtils
 *
 * @author jinhx
 * @since 2021-08-06
 */
public class NacosUtils {

    private static NacosConfigService nacosConfigService;

    static {
        nacosConfigService = SpringUtils.getBean(NacosConfigService.class);
    }

    /**
     * 日志输出控制开关
     *
     * @return true：打开，false：关闭
     */
    public static boolean getMDCLogSwitch() {
        try {
            return nacosConfigService.getMDCLogSwitch();
        } catch (Exception e) {
            return true;
        }
    }

}
