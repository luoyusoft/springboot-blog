package com.jinhx.blog.entity.timeline;

import lombok.Data;

import java.util.List;

/**
 * Timeline
 *
 * @author jinhx
 * @since 2019-02-22
 */
@Data
public class Timeline {

    private Integer year;

    private Integer count;

    private Boolean open;

    private List<TimelineMonth> months;

}
