package com.jinhx.blog.entity.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;

/**
 * QueryPage
 *
 * @author jinhx
 * @since 2018-10-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryPage<T> extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = -6559075988397185459L;

    /**
     * mybatis-plus分页参数
     */
    private Page<T> page;

    /**
     * 当前页码
     */
    private long currPage = 1;

    /**
     * 每页条数
     */
    private int limit = 10;

    public QueryPage(Integer initialPage, Integer initialLimit){
        this.put("page", initialPage);
        this.put("limit", initialLimit);

        //分页参数
        if(initialPage != null){
            currPage = initialPage;
        }

        if(initialLimit != null){
            limit = initialLimit;
        }

        this.put("offset", (currPage - 1) * limit);
        this.put("page", currPage);
        this.put("limit", limit);

        // mybatis-plus分页
        page = new Page<>(currPage, limit);
    }

}
