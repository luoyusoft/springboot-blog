package com.jinhx.blog.service.sys.impl;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.sys.SysParam;
import com.jinhx.blog.service.sys.SysParamMapperService;
import com.jinhx.blog.service.sys.SysParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SysParamServiceImpl
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public class SysParamServiceImpl implements SysParamService {

    @Autowired
    private SysParamMapperService sysParamMapperService;

    /**
     * 分页查询系统参数列表
     *
     * @param page page
     * @param limit limit
     * @param menuUrl menuUrl
     * @param type type
     * @return 系统参数列表
     */
    @Override
    public PageData<SysParam> selectPage(Integer page, Integer limit, String menuUrl, String type) {
        return sysParamMapperService.selectPage(page, limit, menuUrl, type);
    }

    /**
     * 查询所有参数列表
     *
     * @return 参数列表
     */
    @Override
    public List<SysParam> selectAllSysParams() {
        return sysParamMapperService.list();
    }

    /**
     * 根据sysParamId查询参数
     *
     * @param sysParamId sysParamId
     * @return 参数
     */
    @Override
    public SysParam selectSysParamById(Long sysParamId) {
        return sysParamMapperService.selectSysParamById(sysParamId);
    }

    /**
     * 新增参数
     *
     * @param sysParam sysParam
     */
    @Override
    public void insertSysParam(SysParam sysParam) {
        sysParamMapperService.insertSysParam(sysParam);
    }

    /**
     * 根据sysParamId更新参数
     *
     * @param sysParam sysParam
     */
    @Override
    public void updateSysParamById(SysParam sysParam) {
        sysParamMapperService.updateSysParamById(sysParam);
    }

    /**
     * 批量根据sysParamId删除参数
     *
     * @param sysParamIds sysParamIds
     */
    public void deleteSysParamsById(List<Long> sysParamIds) {
        sysParamMapperService.deleteSysParamsById(sysParamIds);
    }

}
