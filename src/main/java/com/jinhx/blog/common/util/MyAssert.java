package com.jinhx.blog.common.util;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * MyAssert
 *
 * @author jinhx
 * @since 2021-08-25
 */
public class MyAssert {

    public MyAssert() {
    }

    /********************** boolean start ********************************/

    public static void isTrue(boolean expression) {
        if (!expression) {
            throw new MyException(ResponseEnums.PARAM_ERROR);
        }
    }

    public static void isTrue(boolean expression, ResponseEnums responseEnums) {
        if (!expression) {
            throw new MyException(responseEnums);
        }
    }

    public static void isTrue(boolean expression, String msg) {
        if (!expression) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), msg);
        }
    }

    /********************** boolean end ********************************/

    /********************** Object start ********************************/

    public static <T> void notNull(T object) {
        if (Objects.isNull(object)) {
            throw new MyException(ResponseEnums.PARAM_ERROR);
        }
    }

    public static <T> void notNull(T object, ResponseEnums responseEnums) {
        if (Objects.isNull(object)) {
            throw new MyException(responseEnums);
        }
    }

    public static <T> void notNull(T object, String msg) {
        if (Objects.isNull(object)) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), msg);
        }
    }

    /********************** Object end ********************************/

    /********************** String start ********************************/

    public static void notBlank(String text) {
        if (StringUtils.isBlank(text)) {
            throw new MyException(ResponseEnums.PARAM_ERROR);
        }
    }

    public static void notBlank(String text, ResponseEnums responseEnums) {
        if (StringUtils.isBlank(text)) {
            throw new MyException(responseEnums);
        }
    }

    public static void notBlank(String text, String msg) {
        if (StringUtils.isBlank(text)) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), msg);
        }
    }

    public static void notEmpty(String str) {
        if (StringUtils.isEmpty(str)) {
            throw new MyException(ResponseEnums.PARAM_ERROR);
        }
    }

    public static void notEmpty(String str, ResponseEnums responseEnums) {
        if (StringUtils.isEmpty(str)) {
            throw new MyException(responseEnums);
        }
    }

    public static void notEmpty(String str, String msg) {
        if (StringUtils.isEmpty(str)) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), msg);
        }
    }

    /********************** String end ********************************/

    /********************** 数组 start ********************************/

    public static void notEmpty(Object[] array) {
        if (ObjectUtils.isEmpty(array)) {
            throw new MyException(ResponseEnums.PARAM_ERROR);
        }
    }

    public static void notEmpty(Object[] array, ResponseEnums responseEnums) {
        if (ObjectUtils.isEmpty(array)) {
            throw new MyException(responseEnums);
        }
    }

    public static void notEmpty(Object[] array, String msg) {
        if (ObjectUtils.isEmpty(array)) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), msg);
        }
    }

    /********************** 数组 end ********************************/

    /********************** Collection start ********************************/

    public static void sizeBetween(Collection collection, int min, int max) {
        if (CollectionUtils.isEmpty(collection) || collection.size() < min || collection.size() > max) {
            throw new MyException(ResponseEnums.PARAM_ERROR);
        }
    }

    public static void sizeBetween(Collection collection, int min, int max, ResponseEnums responseEnums) {
        if (CollectionUtils.isEmpty(collection) || collection.size() < min || collection.size() > max) {
            throw new MyException(responseEnums);
        }
    }

    public static void sizeBetween(Collection collection, int min, int max, String msg) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), msg + "不能为空");
        }
        if (collection.size() < min) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), msg + "不能少于" + min + "个");
        }
        if (collection.size() > max) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), msg + "不能超过" + max + "个");
        }
    }

    public static void sizeMin(Collection collection, int min) {
        if (CollectionUtils.isEmpty(collection) || collection.size() < min) {
            throw new MyException(ResponseEnums.PARAM_ERROR);
        }
    }

    public static void sizeMin(Collection collection, int min, ResponseEnums responseEnums) {
        if (CollectionUtils.isEmpty(collection) || collection.size() < min) {
            throw new MyException(responseEnums);
        }
    }

    public static void sizeMin(Collection collection, int min, String msg) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), msg + "不能为空");
        }
        if (collection.size() < min) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), msg + "不能少于" + min + "个");
        }
    }

    public static void sizeMax(Collection collection, int max) {
        if (CollectionUtils.isEmpty(collection) || collection.size() > max) {
            throw new MyException(ResponseEnums.PARAM_ERROR);
        }
    }

    public static void sizeMax(Collection collection, int max, ResponseEnums responseEnums) {
        if (CollectionUtils.isEmpty(collection) || collection.size() > max) {
            throw new MyException(responseEnums);
        }
    }

    public static void sizeMax(Collection collection, int max, String msg) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), msg + "不能为空");
        }
        if (collection.size() > max) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), msg + "不能超过" + max + "个");
        }
    }

    public static void notEmpty(Collection collection) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new MyException(ResponseEnums.PARAM_ERROR);
        }
    }

    public static void notEmpty(Collection collection, ResponseEnums responseEnums) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new MyException(responseEnums);
        }
    }

    public static void notEmpty(Collection collection, String msg) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), msg);
        }
    }

    /********************** Collection end ********************************/

    /********************** Map start ********************************/

    public static void notEmpty(Map map) {
        if (CollectionUtils.isEmpty(map)) {
            throw new MyException(ResponseEnums.PARAM_ERROR);
        }
    }

    public static void notEmpty(Map map, ResponseEnums responseEnums) {
        if (CollectionUtils.isEmpty(map)) {
            throw new MyException(responseEnums);
        }
    }

    public static void notEmpty(Map map, String msg) {
        if (CollectionUtils.isEmpty(map)) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), msg);
        }
    }

    /********************** Map end ********************************/

}
