package com.jinhx.blog.entity.timeline;

import lombok.Data;

import java.util.List;

/**
 * TimelineMonth
 *
 * @author jinhx
 * @since 2019-02-22
 */
@Data
public class TimelineMonth {

    private Integer month;

    private Integer count;

    private List<TimelinePost> posts;

}
