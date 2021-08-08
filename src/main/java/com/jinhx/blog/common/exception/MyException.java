package com.jinhx.blog.common.exception;

import com.jinhx.blog.common.enums.ResponseEnums;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MyException
 *
 * @author jinhx
 * @since 2018-08-06
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class MyException extends RuntimeException {

    private String msg;
    private int code = 500;

    public MyException(){
        super(ResponseEnums.UNKNOWN.getMsg());
        msg = ResponseEnums.UNKNOWN.getMsg();
    }

    public MyException(ResponseEnums responseEnums, Throwable e){
        super(responseEnums.getMsg(), e);
        msg = responseEnums.getMsg();
        code = responseEnums.getCode();
    }

    public MyException(ResponseEnums responseEnums){
        msg = responseEnums.getMsg();
        code = responseEnums.getCode();
    }

    public MyException(int code, String msg){
        this.msg = msg;
        this.code = code;
    }

}
