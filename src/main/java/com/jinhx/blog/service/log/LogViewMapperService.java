package com.jinhx.blog.service.log;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.common.api.IPApi;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.log.LogView;
import com.jinhx.blog.entity.sys.IPInfo;
import com.jinhx.blog.mapper.log.LogViewMapper;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * LogViewMapperService
 *
 * @author jinhx
 * @since 2019-02-24
 */
@Slf4j
@Service
public class LogViewMapperService extends ServiceImpl<LogViewMapper, LogView> {

    @Autowired
    private IPApi ipApi;

    /**
     * 分页查询日志
     *
     * @param page page
     * @param limit limit
     * @param module module
     * @return PageUtils
     */
    public PageData queryPage(Integer page, Integer limit, Integer module) {
        IPage<LogView> logViewIPage = baseMapper.selectPage(new QueryPage<LogView>(page, limit).getPage(),
                new LambdaQueryWrapper<LogView>()
                        .eq(ObjectUtil.isNotNull(module), LogView::getModule, module)
                        .orderByDesc(LogView::getCreateTime)
        );
        return new PageData(logViewIPage);
    }

    /**
     * 清洗城市信息
     */
    public void cleanCityInfo() {
        log.info("开始清洗log_view表");
        XxlJobLogger.log("开始清洗log_view表");

        LogView maxLogView = baseMapper.selectList(new LambdaQueryWrapper<LogView>()
                .eq(LogView::getCountry, null)
                .or()
                .eq(LogView::getRegion, null)
                .or()
                .eq(LogView::getCity, null)
                .orderByDesc(LogView::getId)
                .last("limit 1")).get(0);

        if (maxLogView == null){
            return;
        }

        Integer maxId = maxLogView.getId();

        for (int start = 0, end = 500; start < maxId; start += 500, end += 500) {
            List<LogView> logViews = baseMapper.selectList(new LambdaQueryWrapper<LogView>()
                    .ge(LogView::getId, start)
                    .le(LogView::getId, end)
                    .and(lqw ->
                            lqw.eq(LogView::getCountry, null)
                                    .or()
                                    .eq(LogView::getRegion, null)
                                    .or()
                                    .eq(LogView::getCity, null)));

            logViews.forEach(logViewsItem -> {
                try {
                    IPInfo ipInfo = ipApi.getIpInfo(logViewsItem.getIp());
                    LogView logView = new LogView();
                    logView.setId(logViewsItem.getId());
                    logView.setCountry(ipInfo.getCountry());
                    logView.setRegion(ipInfo.getRegionName());
                    logView.setCity(ipInfo.getCity());

                    baseMapper.updateById(logView);

                    log.info("清洗成功：{}", logViewsItem);
                    XxlJobLogger.log("清洗成功：{}", logViewsItem.toString());
                    Thread.sleep(1000);
                }catch (Exception e){
                    log.info("清洗失败：" + e);
                    XxlJobLogger.log("清洗失败：" + e);
                }
            });
        }
        log.info("清洗log_view表结束");
        XxlJobLogger.log("清洗log_view表结束");
    }

}
