package com.jinhx.blog.controller.timeline;

import com.jinhx.blog.common.aop.annotation.LogView;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.timeline.Timeline;
import com.jinhx.blog.service.timeline.TimelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * TimelineController
 *
 * @author jinhx
 * @since 2019-10-08
 */
@RestController
public class TimelineController {

    @Autowired
    private TimelineService timelineService;

    /********************** portal ********************************/

    /**
     * 查询时间线列表
     *
     * @return 时间线列表
     */
    @GetMapping("/timeline/listtimelines")
    @LogView(module = 4)
    public Response<List<Timeline>> selectTimelines() {
        return Response.success(timelineService.selectTimelines());
    }

}
