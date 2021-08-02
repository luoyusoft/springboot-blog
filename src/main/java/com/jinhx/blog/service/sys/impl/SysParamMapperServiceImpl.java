package com.jinhx.blog.service.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.common.util.PageUtils;
import com.jinhx.blog.common.util.Query;
import com.jinhx.blog.entity.sys.SysParam;
import com.jinhx.blog.service.sys.SysParamMapperService;
import com.jinhx.blog.mapper.sys.SysParamMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统参数 服务实现类
 * </p>
 *
 * @author luoyu
 * @since 2018-12-28
 */
@Service
@Slf4j
public class SysParamMapperServiceImpl extends ServiceImpl<SysParamMapper, SysParam> implements SysParamMapperService {

    /**
     * 分页查询
     *
     * @param page page
     * @param limit limit
     * @param menuUrl menuUrl
     * @param type type
     * @return PageUtils
     */
    @Override
    public PageUtils queryPage(Integer page, Integer limit, String menuUrl, String type) {
        IPage<SysParam> paramIPage = baseMapper.selectPage(new Query<SysParam>(page, limit).getPage(),
                new LambdaQueryWrapper<SysParam>()
                        .eq(StringUtils.isNotBlank(menuUrl), SysParam::getMenuUrl,menuUrl)
                        .like(StringUtils.isNotBlank(String.valueOf(type)), SysParam::getType,type));
        return new PageUtils(paramIPage);
    }

}
