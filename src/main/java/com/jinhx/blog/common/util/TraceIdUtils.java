package com.jinhx.blog.common.util;

import cn.hutool.core.util.IdUtil;

/**
 * TraceIdUtils
 *
 * @author jinhx
 * @since 2021-08-06
 */
public class TraceIdUtils {

    /**
     * traceId
     */
    public static final String TRACE_ID = "traceId";

    public static String getTraceId() {
        return IdUtil.simpleUUID();
    }

}