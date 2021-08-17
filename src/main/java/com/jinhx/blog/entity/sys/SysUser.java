package com.jinhx.blog.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jinhx.blog.common.validator.group.AddGroup;
import com.jinhx.blog.common.validator.group.UpdateGroup;
import com.jinhx.blog.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * SysUser
 *
 * @author jinhx
 * @since 2018-10-08
 */
@Data
@Component
@TableName("sys_user")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="SysUser对象", description="用户管理")
public class SysUser extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 3285754866443165412L;

    // 用户默认头像地址
    public static String sysUserDefaultProfile;

    @Value("${sys.user.default.profile}")
    public void setSysUserDefaultProfile(String sysUserDefaultProfile) {
        SysUser.sysUserDefaultProfile = sysUserDefaultProfile;
    }

    @NotBlank(message = "用户名不能为空" , groups = {AddGroup.class, UpdateGroup.class})
    @Length(min = 4, max = 20, message = "用户名长度必须位于4到20之间", groups = {AddGroup.class, UpdateGroup.class})
    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "用户状态（0：禁用，1：正常）")
    private Integer status;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "手机")
    private String mobile;

    @ApiModelProperty(value = "密码盐")
    private String salt;

    @NotBlank(message = "昵称不能为空" ,groups = {AddGroup.class, UpdateGroup.class})
    @Length(min = 2, max = 20, message = "昵称长度必须位于2到20之间", groups = {AddGroup.class, UpdateGroup.class})
    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "头像")
    private String profile;

}
