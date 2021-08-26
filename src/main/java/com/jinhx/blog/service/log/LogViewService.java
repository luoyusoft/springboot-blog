package com.jinhx.blog.service.log;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.log.LogView;
import com.jinhx.blog.entity.log.vo.HomeLogInfoVO;

/**
 * LogViewServiceImpl
 *
 * @author jinhx
 * @since 2019-02-24
 */
public interface LogViewService {

    /**
     * 查询首页信息
     *
     * @return 首页信息
     */
    HomeLogInfoVO selectHommeLogInfoVO();

    /**
     * 分页查询日志列表
     *
     * @param page page
     * @param limit limit
     * @param module module
     * @return 日志列表
     */
    PageData<LogView> selectPage(Integer page, Integer limit, Integer module);

}
