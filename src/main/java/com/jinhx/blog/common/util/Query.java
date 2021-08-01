package com.jinhx.blog.common.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.LinkedHashMap;

/**
 * Query
 *
 * @author jinhx
 * @since 2018-10-21
 */
@Data
public class Query<T> extends LinkedHashMap<String, Object> {

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

    public Query(Integer initialPage, Integer initialLimit){
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
