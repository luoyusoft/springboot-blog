package com.jinhx.blog.entity.operation;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jinhx.blog.common.validator.group.AddGroup;
import com.jinhx.blog.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * FriendLink
 *
 * @author jinhx
 * @since 2019-02-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="FriendLink对象", description="友链")
@TableName("friend_link")
public class FriendLink extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -5450078578713186942L;

    @ApiModelProperty(value = "链接名称")
    @NotBlank(message = "链接名称不能为空", groups = {AddGroup.class})
    private String title;

    @ApiModelProperty(value = "链接地址")
    @NotBlank(message = "链接地址不能为空", groups = {AddGroup.class})
    private String url;

    @ApiModelProperty(value = "链接头像")
    private String avatar;

    @ApiModelProperty(value = "链接简介")
    private String description;

}
