package com.jinhx.blog.entity.sys.vo;

import com.jinhx.blog.common.validator.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * PasswordVO
 *
 * @author jinhx
 * @since 2018-10-26
 */
@Data
public class PasswordVO {

    private String password;

    private Long sysUserId;

    @Length(min = 6, message="新密码长度不能低于6位", groups = {UpdateGroup.class})
    @NotBlank(message="新密码不能为空", groups = {UpdateGroup.class})
    private String newPassword;

}
