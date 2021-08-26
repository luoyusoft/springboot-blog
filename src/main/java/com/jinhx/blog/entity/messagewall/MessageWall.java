package com.jinhx.blog.entity.messagewall;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

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
@EqualsAndHashCode(callSuper = true)
@TableName("message_wall")
public class MessageWall extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 4369479038696400174L;

    public static final Long REPLY_ID_LAYER_MASTER = -1L;

    public static final Long CREATER_UPDATER_GUEST_ID = -1L;

    public static final String CREATER_UPDATER_GUEST_NAME = "游客";

    public static final String NAME_PREFIX = "游客-";

    /**
     * 主键
     */
    @ApiModelProperty(value = "留言墙id主键")
    @TableId(type = IdType.INPUT)
    private Long messageWallId;

    @ApiModelProperty(value = "楼层数")
    private Integer floorNum;

    @ApiModelProperty(value = "回复id，-1为层主")
    private Long replyId;

    @ApiModelProperty(value = "昵称")
    @NotBlank(message = "昵称不能为空", groups = {InsertGroup.class})
    @Length(max = 50, message = "昵称长度不能超过50", groups = {InsertGroup.class})
    private String name;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "内容")
    @NotBlank(message = "内容不能为空", groups = {InsertGroup.class})
    @Length(max = 2000, message = "内容长度不能超过2000", groups = {InsertGroup.class})
    private String comment;

    @ApiModelProperty(value = "头像地址")
    private String profile;

    @ApiModelProperty(value = "网站")
    private String website;

}
