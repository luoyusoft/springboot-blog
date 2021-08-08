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
@EqualsAndHashCode(callSuper = true)
@Data
public class TagVO extends Tag {

    private static final long serialVersionUID = -8122783307268537407L;

    private String linkNum;

}
