package com.jinhx.blog.common.filter.params;

import com.jinhx.blog.common.util.RequestReadUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * ParamsHttpServletRequestWrapper
 *
 * @author jinhx
 * @since 2019-08-06
 */
public class ParamsHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final String body;

    public ParamsHttpServletRequestWrapper(HttpServletRequest httpServletRequest) throws IOException {
        super(httpServletRequest);
        body = RequestReadUtils.read(httpServletRequest);
    }

    public String getBody() {
        return body;
    }

    @Override
    public ServletInputStream getInputStream()  {

        final ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes());

        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {}

            @Override
            public int read(){
                return bais.read();
            }

        };
    }

    @Override
    public BufferedReader getReader(){
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

}
