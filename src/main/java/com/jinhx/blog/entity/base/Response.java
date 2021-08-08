package com.jinhx.blog.entity.base;

import com.jinhx.blog.common.enums.ResponseEnums;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Response
 *
 * @author jinhx
 * @since 2018-11-07
 */
@Data
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 3746739103117954317L;

    private String msg;

    private int code;

    private T data;

    private String time;

    private String srs;

    private Response() {
    }

    private Response(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    private Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    public static <T> Response<T> success() {
        return new Response<>(ResponseEnums.SUCCESS.getCode(), ResponseEnums.SUCCESS.getMsg());
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(ResponseEnums.SUCCESS.getCode(), ResponseEnums.SUCCESS.getMsg(), data);
    }

    public static <T> Response<T> success(T data, String msg) {
        return new Response<>(ResponseEnums.SUCCESS.getCode(), msg, data);
    }

    public static <T> Response<T> fail() {
        return new Response<>(ResponseEnums.UNKNOWN.getCode(), ResponseEnums.UNKNOWN.getMsg());
    }

    public static <T> Response<T> fail(ResponseEnums responseEnums) {
        return new Response<>(responseEnums.getCode(), responseEnums.getMsg());
    }

    public static <T> Response<T> fail(ResponseEnums responseEnums, T data) {
        return new Response<>(responseEnums.getCode(), responseEnums.getMsg(), data);
    }

    public static <T> Response<T> fail(int code, String msg) {
        return new Response<>(code, msg);
    }

    public static <T> Response<T> fail(int code, String msg, T data) {
        return new Response<>(code, msg, data);
    }

}
