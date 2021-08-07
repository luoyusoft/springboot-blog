package com.jinhx.blog.entity.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * BaseRequestDTO
 *
 * @author jinhx
 * @since 2021-08-06
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class BaseRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * logStr
     */
    private String logStr;

}
