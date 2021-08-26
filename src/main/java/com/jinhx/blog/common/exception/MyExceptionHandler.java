package com.jinhx.blog.common.exception;

import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.entity.base.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * MyExceptionHandler
 *
 * @author jinhx
 * @since 2018-08-06
 */
@RestControllerAdvice
@Slf4j
public class MyExceptionHandler {

    /**
     * 处理异常
     *
     * @param e e
     * @return Response
     */
    @ExceptionHandler(MyException.class)
    public <T> Response<T> handleMyException(MyException e){
        log.error(ExceptionUtils.getStackTrace(e));
        return Response.fail(e.getCode(), e.getMsg());
    }

    /**
     * 处理异常
     *
     * @param e e
     * @return Response
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public <T> Response<T> handlerNoFoundException(Exception e){
        log.error(ExceptionUtils.getStackTrace(e));
        return Response.fail(ResponseEnums.PATH_NOT_FOUND);
    }

    /**
     * 处理异常
     *
     * @param e e
     * @return Response
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public <T> Response<T> handleDuplicateKeyException(DuplicateKeyException e){
        log.error(ExceptionUtils.getStackTrace(e));
        return Response.fail(ResponseEnums.DUPLICATE_KEY);
    }

    /**
     * 处理异常
     *
     * @param e e
     * @return Response
     */
    @ExceptionHandler(AuthorizationException.class)
    public <T> Response<T> handleAuthorizationException(AuthorizationException e){
        log.error(ExceptionUtils.getStackTrace(e));
        return Response.fail(ResponseEnums.NO_AUTH);
    }

    /**
     * 处理异常
     *
     * @param e e
     * @return Response
     */
    @ExceptionHandler(Exception.class)
    public <T> Response<T> handleException(Exception e){
        log.error(ExceptionUtils.getStackTrace(e));
        return Response.fail();
    }

}
