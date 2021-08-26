package com.jinhx.blog.service.sys;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.sys.SysParam;

import java.util.List;

/**
 * SysParamService
 *
 * @author jinhx
 * @since 2018-10-22
 */
public interface SysParamService {

    /**
     * 分页查询系统参数列表
     *
     * @param page page
     * @param limit limit
     * @param menuUrl menuUrl
     * @param type type
     * @return 系统参数列表
     */
    PageData<SysParam> selectPage(Integer page, Integer limit, String menuUrl, String type);

    /**
     * 查询所有参数列表
     *
     * @return 参数列表
     */
    List<SysParam> selectAllSysParams();

    /**
     * 根据sysParamId查询参数
     *
     * @param sysParamId sysParamId
     * @return 参数
     */
    SysParam selectSysParamById(Long sysParamId);

    /**
     * 新增参数
     *
     * @param sysParam sysParam
     */
    void insertSysParam(SysParam sysParam);

    /**
     * 根据sysParamId更新参数
     *
     * @param sysParam sysParam
     */
    void updateSysParamById(SysParam sysParam);

    /**
     * 批量根据sysParamId删除参数
     *
     * @param sysParamIds sysParamIds
     */
    void deleteSysParamsById(List<Long> sysParamIds);

}
