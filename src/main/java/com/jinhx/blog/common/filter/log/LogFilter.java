package com.jinhx.blog.common.filter.log;

import com.jinhx.blog.common.util.TraceIdUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * LogFilter
 *
 * @author jinhx
 * @since 2021-08-06
 */
public class LogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            String traceId = TraceIdUtils.getTraceId();
            MDC.put(TraceIdUtils.TRACE_ID, traceId);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            httpServletResponse.addHeader(TraceIdUtils.TRACE_ID, traceId);
        } finally {
            MDC.clear();
        }
    }

}
