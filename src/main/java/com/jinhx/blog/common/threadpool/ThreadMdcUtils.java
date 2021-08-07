package com.jinhx.blog.common.threadpool;

import com.jinhx.blog.common.util.TraceIdUtils;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * ThreadMdcUtils
 *
 * @author jinhx
 * @since 2021-08-06
 */
public class ThreadMdcUtils {

    public static void setTraceIdIfAbsent() {
        if (MDC.get(TraceIdUtils.TRACE_ID) == null) {
            MDC.put(TraceIdUtils.TRACE_ID, TraceIdUtils.getTraceId());
        }
    }

    public static <T> Callable<T> wrap(final Callable<T> callable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent();
            try {
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }

    public static Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent();
            try {
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}