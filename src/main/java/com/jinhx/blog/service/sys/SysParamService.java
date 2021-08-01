package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.common.util.PageUtils;
import com.jinhx.blog.entity.sys.SysParam;

/**
 * <p>
 * 系统参数 服务类
 * </p>
 *
 * @author luoyu
 * @since 2018-12-28
 */
public interface SysParamService extends IService<SysParam> {

    /**
     * 分页查询
     *
     * @param page page
     * @param limit limit
     * @param menuUrl menuUrl
     * @param type type
     * @return PageUtils
     */
     PageUtils queryPage(Integer page, Integer limit, String menuUrl, String type);

}
