package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.sys.SysParam;
import com.jinhx.blog.mapper.sys.SysParamMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * SysParamMapperService
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
@Slf4j
public class SysParamMapperService extends ServiceImpl<SysParamMapper, SysParam> {

    /**
     * 分页查询
     *
     * @param page page
     * @param limit limit
     * @param menuUrl menuUrl
     * @param type type
     * @return PageUtils
     */
    public PageData queryPage(Integer page, Integer limit, String menuUrl, String type) {
        return new PageData(baseMapper.selectPage(new QueryPage<SysParam>(page, limit).getPage(),
                new LambdaQueryWrapper<SysParam>()
                        .eq(StringUtils.isNotBlank(menuUrl), SysParam::getMenuUrl, menuUrl)
                        .like(StringUtils.isNotBlank(String.valueOf(type)), SysParam::getType, type)));
    }

}
