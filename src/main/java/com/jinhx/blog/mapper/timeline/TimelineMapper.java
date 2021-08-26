package com.jinhx.blog.mapper.timeline;

import com.jinhx.blog.entity.timeline.Timeline;
import com.jinhx.blog.entity.timeline.TimelinePost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * TimelineMapper
 *
 * @author jinhx
 * @since 2019-02-24
 */
public interface TimelineMapper {

    List<TimelinePost> selectTimelinePosts(@Param("year") Integer year, @Param("month") Integer month);

    List<Timeline> selectTimelines();

}
