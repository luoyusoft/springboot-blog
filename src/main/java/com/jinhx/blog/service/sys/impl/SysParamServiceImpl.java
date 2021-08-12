package com.jinhx.blog.service.sys.impl;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.sys.SysParam;
import com.jinhx.blog.service.sys.SysParamMapperService;
import com.jinhx.blog.service.sys.SysParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SysParamServiceImpl
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
@Slf4j
public class SysParamServiceImpl implements SysParamService {

    @Autowired
    private SysParamMapperService sysParamMapperService;

    /**
     * 分页查询
     *
     * @param page page
     * @param limit limit
     * @param menuUrl menuUrl
     * @param type type
     * @return PageData
     */
    @Override
    public PageData queryPage(Integer page, Integer limit, String menuUrl, String type) {
        return sysParamMapperService.queryPage(page, limit, menuUrl, type);
    }

    /**
     * 获取所有参数列表
     *
     * @return 所有参数列表
     */
    @Override
    public List<SysParam> list() {
        return sysParamMapperService.list();
    }

    /**
     * 根据角色id列表批量删除角色
     *
     * @param roleIds 角色id列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Integer> roleIds) {
        sysParamMapperService.removeByIds(roleIds);
    }

    /**
     * 修改
     *
     * @param sysParam sysParam
     */
    @Override
    public void updateById(SysParam sysParam) {
        sysParamMapperService.updateById(sysParam);
    }

    /**
     * 保存
     *
     * @param sysParam sysParam
     */
    @Override
    public void save(SysParam sysParam) {
        sysParamMapperService.save(sysParam);
    }

    /**
     * 信息
     *
     * @param id id
     * @return 信息
     */
    @Override
    public SysParam getById(Integer id) {
        return sysParamMapperService.getById(id);
    }

}
