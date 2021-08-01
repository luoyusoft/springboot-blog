package com.jinhx.blog.entity.operation.vo;

import com.jinhx.blog.entity.operation.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TagVO
 *
 * @author jinhx
 * @since 2019-02-17
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class TagVO extends Tag {

    private String linkNum;

}
