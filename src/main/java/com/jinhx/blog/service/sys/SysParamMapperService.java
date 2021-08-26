package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.sys.SysParam;
import com.jinhx.blog.mapper.sys.SysParamMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SysParamMapperService
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public class SysParamMapperService extends ServiceImpl<SysParamMapper, SysParam> {

    /**
     * 分页查询系统参数列表
     *
     * @param page page
     * @param limit limit
     * @param menuUrl menuUrl
     * @param type type
     * @return 系统参数列表
     */
    public PageData<SysParam> selectPage(Integer page, Integer limit, String menuUrl, String type) {
        return new PageData<>(baseMapper.selectPage(new QueryPage<SysParam>(page, limit).getPage(),
                new LambdaQueryWrapper<SysParam>()
                        .eq(StringUtils.isNotBlank(menuUrl), SysParam::getMenuUrl, menuUrl)
                        .like(StringUtils.isNotBlank(String.valueOf(type)), SysParam::getType, type)));
    }

    /**
     * 查询所有参数列表
     *
     * @return 参数列表
     */
    public List<SysParam> selectAllSysParams() {
        return baseMapper.selectList(new LambdaQueryWrapper<>());
    }

    /**
     * 根据sysParamId查询参数
     *
     * @param sysParamId sysParamId
     * @return 参数
     */
    public SysParam selectSysParamById(Long sysParamId) {
        List<SysParam> sysParams = selectSysParamsById(Lists.newArrayList(sysParamId));
        if (CollectionUtils.isEmpty(sysParams)){
            return null;
        }

        return sysParams.get(0);
    }

    /**
     * 根据sysParamId查询参数列表
     *
     * @param sysParamIds sysParamIds
     * @return 参数列表
     */
    public List<SysParam> selectSysParamsById(List<Long> sysParamIds) {
        if (CollectionUtils.isEmpty(sysParamIds)){
            return Lists.newArrayList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<SysParam>().in(SysParam::getSysParamId, sysParamIds));
    }

    /**
     * 根据sysParamId删除参数
     *
     * @param sysParamId sysParamId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysParamById(Long sysParamId) {
        deleteSysParamsById(Lists.newArrayList(sysParamId));
    }

    /**
     * 批量根据sysParamId删除参数
     *
     * @param sysParamIds sysParamIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysParamsById(List<Long> sysParamIds) {
        if (CollectionUtils.isNotEmpty(sysParamIds)){
            if (baseMapper.deleteBatchIds(sysParamIds) != sysParamIds.size()){
                throw new MyException(ResponseEnums.DELETE_FAIL);
            }
        }
    }

    /**
     * 新增参数
     *
     * @param sysParam sysParam
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertSysParam(SysParam sysParam) {
        insertSysParams(Lists.newArrayList(sysParam));
    }

    /**
     * 批量新增参数
     *
     * @param sysParams sysParams
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertSysParams(List<SysParam> sysParams) {
        if (CollectionUtils.isNotEmpty(sysParams)){
            if (sysParams.stream().mapToInt(item -> baseMapper.insert(item)).sum() != sysParams.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

    /**
     * 根据sysParamId更新参数
     *
     * @param sysParam sysParam
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSysParamById(SysParam sysParam) {
        updateSysParamsById(Lists.newArrayList(sysParam));
    }

    /**
     * 批量根据sysParamId更新参数
     *
     * @param sysParams sysParams
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSysParamsById(List<SysParam> sysParams) {
        if (CollectionUtils.isNotEmpty(sysParams)){
            if (sysParams.stream().mapToInt(item -> baseMapper.updateById(item)).sum() != sysParams.size()){
                throw new MyException(ResponseEnums.UPDATE_FAILR);
            }
        }
    }

}
