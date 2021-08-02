package com.jinhx.blog.service.log;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.common.util.PageUtils;
import com.jinhx.blog.entity.log.LogView;
import com.jinhx.blog.entity.log.vo.HomeLogInfoVO;

/**
 * LogViewServiceImpl
 *
 * @author jinhx
 * @since 2019-02-24
 */
public interface LogViewMapperService extends IService<LogView> {

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    HomeLogInfoVO getHommeLogInfoVO();

    /**
     * 分页查询日志
     *
     * @param page page
     * @param limit limit
     * @param module module
     * @return PageUtils
     */
    PageUtils queryPage(Integer page, Integer limit, Integer module);

    /**
     * 清洗城市信息
     */
    void cleanCityInfo();

}
