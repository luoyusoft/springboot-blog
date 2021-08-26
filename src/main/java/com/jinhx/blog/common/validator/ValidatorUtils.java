package com.jinhx.blog.common.validator;

import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ValidatorUtils
 *
 * @author jinhx
 * @since 2018-08-06
 */
public class ValidatorUtils {

    private static final Validator validator;

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * 校验对象，校验不通过，则报MyException异常
     *
     * @param object        待校验对象
     * @param groups        待校验的组
     */
    public static void validateEntity(Object object, Class<?>... groups) throws MyException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            List<String> collect = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
            String msg = StringUtils.join(collect, ",");
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), msg);
        }
    }

}
