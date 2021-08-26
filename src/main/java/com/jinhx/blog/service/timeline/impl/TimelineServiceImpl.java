package com.jinhx.blog.service.timeline.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.entity.timeline.Timeline;
import com.jinhx.blog.entity.timeline.TimelineMonth;
import com.jinhx.blog.entity.timeline.TimelinePost;
import com.jinhx.blog.mapper.timeline.TimelineMapper;
import com.jinhx.blog.service.timeline.TimelineService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * TimelineServiceImpl
 *
 * @author jinhx
 * @since 2019-04-11
 */
@CacheConfig(cacheNames ={RedisKeyConstants.TIMELINES})
@Service
public class TimelineServiceImpl implements TimelineService {

    @Resource
    private TimelineMapper timelineMapper;

    /**
     * 查询时间线列表
     *
     * @return 时间线列表
     */
    @Cacheable
    @Override
    public List<Timeline> selectTimelines() {
        List<Timeline> timelineList = timelineMapper.selectTimelines();
        genTimelineMonth(timelineList);
        return timelineList;
    }

    /**
     * 数据转换
     *
     * @param timelineList 时间线列表
     */
    private void genTimelineMonth(List<Timeline> timelineList) {
        for(Timeline timeline : timelineList) {
            List<TimelineMonth> timelineMonthList = new ArrayList<>();
           for (int i = Calendar.DECEMBER + 1; i > 0; i--) {
               List<TimelinePost> postList = timelineMapper.selectTimelinePosts(timeline.getYear(), i);
               if(CollectionUtils.isEmpty(postList)) {
                   continue;
               }
               TimelineMonth month = new TimelineMonth();
               month.setCount(postList.size());
               month.setMonth(i);
               month.setPosts(postList);
               timelineMonthList.add(month);
           }
           timeline.setMonths(timelineMonthList);
        }
    }

}
