package com.jinhx.blog.entity.gitalk;

import lombok.Data;

/**
 * InitGitalkRequest
 *
 * @author jinhx
 * @since 2018-11-30
 */
@Data
public class InitGitalkRequest {

    /**
     * 地址的相对路径后面的id 例如：1
     */
    private Long id;

    /**
     * 地址的相对路径 例如：article
     */
    private String type;

    /**
     * 标题
     */
    private String title;

}
