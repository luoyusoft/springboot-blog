package com.jinhx.blog.controller.timeline;

import com.jinhx.blog.common.aop.annotation.LogView;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.timeline.Timeline;
import com.jinhx.blog.service.timeline.TimelineService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * TimelineController
 *
 * @author jinhx
 * @since 2019-10-08
 */
@RestController
public class TimelineController {

    @Resource
    private TimelineService timelineService;

    /********************** portal ********************************/

    /**
     * 获取时间线列表
     *
     * @return 时间线列表
     */
    @GetMapping("/timeline/listtimelines")
    @LogView(module = 4)
    public Response listTimelines() {
        List<Timeline> timelineList = timelineService.listTimelines();
        return Response.success(timelineList);
    }

}
