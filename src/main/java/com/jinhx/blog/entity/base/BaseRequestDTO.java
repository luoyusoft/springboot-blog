package com.jinhx.blog.entity.base;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * BaseRequestDTO
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Data
public class BaseRequestDTO implements Serializable {

    private static final long serialVersionUID = 4035426155692378372L;

    /**
     * logStr
     */
    private String logStr;

    public void setLogStr(String logStr){
        if (StringUtils.isBlank(this.logStr)){
            this.logStr = logStr;
        }else {
            this.logStr += logStr;
        }
    }

}
