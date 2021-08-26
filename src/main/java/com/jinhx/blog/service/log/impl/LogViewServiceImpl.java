package com.jinhx.blog.service.log.impl;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.log.LogView;
import com.jinhx.blog.entity.log.vo.HomeLogInfoVO;
import com.jinhx.blog.service.log.LogViewMapperService;
import com.jinhx.blog.service.log.LogViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * LogViewServiceImpl
 *
 * @author jinhx
 * @since 2019-02-24
 */
@Service
public class LogViewServiceImpl implements LogViewService {

    @Autowired
    private LogViewMapperService logViewMapperService;

    /**
     * 查询首页信息
     *
     * @return 首页信息
     */
    @Override
    public HomeLogInfoVO selectHommeLogInfoVO() {
        return logViewMapperService.selectHommeLogInfoVO();
    }

    /**
     * 分页查询日志列表
     *
     * @param page page
     * @param limit limit
     * @param module module
     * @return 日志列表
     */
    @Override
    public PageData<LogView> selectPage(Integer page, Integer limit, Integer module) {
        return logViewMapperService.selectPage(page, limit, module);
    }

}
