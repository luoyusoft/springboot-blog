package com.jinhx.blog.common.filter.params;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * ParamsFilter
 *
 * @author jinhx
 * @since 2019-08-06
 */
public class ParamsFilter extends OncePerRequestFilter {

    // 文件传输相关接口不需要二次获取参数
    private final static List<String> UPLOAD_URLS = Arrays.asList("/manage/file/minio/upload", "/manage/file/qiniuyun/upload", "/manage/file/minio/chunkUpload");

    @Override
    public void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        for (String item : UPLOAD_URLS){
            if (item.equals(httpServletRequest.getServletPath())){
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }
        }
        //取Body数据
        ParamsHttpServletRequestWrapper requestWrapper = new ParamsHttpServletRequestWrapper(httpServletRequest);
        filterChain.doFilter(requestWrapper, httpServletResponse);
    }

}
