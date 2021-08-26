package com.jinhx.blog.common.validator.group;

import javax.validation.GroupSequence;

/**
 * 定义校验顺序，如果前面的组失败，则后面不会再校验
 *
 * @author jinhx
 * @since 2020-08-06
 */
@GroupSequence({SelectGroup.class, InsertGroup.class, UpdateGroup.class, DeleteGroup.class})
public interface Group {

}
