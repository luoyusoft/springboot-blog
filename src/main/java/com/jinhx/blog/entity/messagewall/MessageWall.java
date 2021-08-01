package com.jinhx.blog.entity.messagewall;

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
 * MessageWall
 *
 * @author jinhx
 * @since 2018-11-07
 */
@Data
@ApiModel(value="MessageWall对象", description="留言墙")
@EqualsAndHashCode(callSuper = false)
@TableName("message_wall")
public class MessageWall extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Integer REPLY_ID_LAYER_MASTER = -1;

    public static final Integer CREATER_UPDATER_GUEST_ID = -1;

    public static final String CREATER_UPDATER_GUEST_NAME = "游客";

    public static final String NAME_PREFIX = "游客-";

    @ApiModelProperty(value = "楼层数")
    private Integer floorNum;

    @ApiModelProperty(value = "回复id，-1为层主")
    private Integer replyId;

    @ApiModelProperty(value = "昵称")
    @NotBlank(message = "昵称不能为空", groups = {AddGroup.class})
    private String name;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "内容")
    @NotBlank(message = "内容不能为空", groups = {AddGroup.class})
    private String comment;

    @ApiModelProperty(value = "头像地址")
    private String profile;

    @ApiModelProperty(value = "网站")
    private String website;

}
