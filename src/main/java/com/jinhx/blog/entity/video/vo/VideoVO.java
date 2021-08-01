package com.jinhx.blog.entity.video.vo;

import com.jinhx.blog.entity.operation.Tag;
import com.jinhx.blog.entity.video.Video;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * VideoVO
 *
 * @author jinhx
 * @since 2019-02-22
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class VideoVO extends Video {

    /**
     * 所属分类，以逗号分隔
     */
    private String categoryListStr;

    /**
     * 所属标签
     */
    private List<Tag> tagList;

    /**
     * 推荐
     */
    private Boolean recommend;

    /**
     * 置顶
     */
    private Boolean top;

    /**
     * 上传者
     */
    private String author;

}
