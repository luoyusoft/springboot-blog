package com.jinhx.blog.service.log;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.log.LogView;
import com.jinhx.blog.entity.log.vo.HomeLogInfoVO;
import com.jinhx.blog.mapper.log.LogViewMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/**
 * LogViewMapperService
 *
 * @author jinhx
 * @since 2019-02-24
 */
@Service
public class LogViewMapperService extends ServiceImpl<LogViewMapper, LogView> {

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    public HomeLogInfoVO selectHommeLogInfoVO() {
        Integer allPV = baseMapper.selectCount(new LambdaQueryWrapper<>());
        Integer allUV = baseMapper.selectCount(new QueryWrapper<LogView>()
                .select("distinct ip,browser_name,browser_version,device_manufacturer,device_type,os_version"));

        // 当天零点
        LocalDateTime createTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Integer todayPV = baseMapper.selectCount(new LambdaQueryWrapper<LogView>()
                .ge(LogView::getCreateTime, createTime));
        Integer todayUV = baseMapper.selectCount(new QueryWrapper<LogView>()
                .select("distinct ip,browser_name,browser_version,device_manufacturer,device_type,os_version")
                .lambda()
                .ge(LogView::getCreateTime, createTime));

        HomeLogInfoVO homeLogInfoVO = new HomeLogInfoVO();
        homeLogInfoVO.setAllPV(allPV);
        homeLogInfoVO.setAllUV(allUV);
        homeLogInfoVO.setTodayPV(todayPV);
        homeLogInfoVO.setTodayUV(todayUV);
        return homeLogInfoVO;
    }

    /**
     * 分页查询日志列表
     *
     * @param page page
     * @param limit limit
     * @param module module
     * @return 日志列表
     */
    public PageData<LogView> selectPage(Integer page, Integer limit, Integer module) {
        return new PageData<>(baseMapper.selectPage(new QueryPage<LogView>(page, limit).getPage(),
                new LambdaQueryWrapper<LogView>()
                        .eq(Objects.nonNull(module), LogView::getModule, module)
                        .orderByDesc(LogView::getCreateTime)));
    }

    /**
     * 根据articleId更新文章
     *
     * @param logView logView
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateLogViewById(LogView logView) {
        updateLogViewsById(Lists.newArrayList(logView));
    }

    /**
     * 批量根据logViewId更新文章
     *
     * @param logViews logViews
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateLogViewsById(List<LogView> logViews) {
        if (CollectionUtils.isNotEmpty(logViews)){
            if (logViews.stream().mapToInt(item -> baseMapper.updateById(item)).sum() != logViews.size()){
                throw new MyException(ResponseEnums.UPDATE_FAILR);
            }
        }
    }

    /**
     * 根据logViewId范围查询城市信息为空日志列表
     *
     * @param start start
     * @param end end
     * @return 日志列表
     */
    public List<LogView> selectNullCityLogViewsByIdRange(Long start, Long end) {
        return baseMapper.selectList(new LambdaQueryWrapper<LogView>()
                .ge(LogView::getLogViewId, start)
                .le(LogView::getLogViewId, end)
                .and(lqw ->
                        lqw.eq(LogView::getCountry, null)
                                .or()
                                .eq(LogView::getRegion, null)
                                .or()
                                .eq(LogView::getCity, null)));
    }

    /**
     * 查询最大日志id
     *
     * @return 最大日志id
     */
    public Long selectMaxLogViewId() {
        LogView maxLogView = baseMapper.selectList(new LambdaQueryWrapper<LogView>()
                .eq(LogView::getCountry, null)
                .or()
                .eq(LogView::getRegion, null)
                .or()
                .eq(LogView::getCity, null)
                .orderByDesc(LogView::getLogViewId)
                .last("limit 1")).get(0);

        if (Objects.isNull(maxLogView)){
            return null;
        }

        return maxLogView.getLogViewId();
    }

    /**
     * 新增日志
     *
     * @param logView logView
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertLogView(LogView logView) {
        insertLogViews(Lists.newArrayList(logView));
    }

    /**
     * 批量新增日志
     *
     * @param logViews logViews
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertLogViews(List<LogView> logViews) {
        if (CollectionUtils.isNotEmpty(logViews)){
            if (logViews.stream().mapToInt(item -> baseMapper.insert(item)).sum() != logViews.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

}
