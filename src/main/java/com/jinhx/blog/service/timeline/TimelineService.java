package com.jinhx.blog.service.timeline;

import com.jinhx.blog.entity.timeline.Timeline;

import java.util.List;

/**
 * TimelineService
 *
 * @author jinhx
 * @since 2019-04-11
 */
public interface TimelineService {

    /**
     * 查询时间线列表
     *
     * @return 时间线列表
     */
    List<Timeline> selectTimelines();

}
