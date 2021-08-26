package com.jinhx.blog.common.util;

import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.sys.vo.SysUserVO;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SysAdminUtils
 *
 * @author jinhx
 * @since 2019-06-07
 */
@Component
public class SysAdminUtils {

    // 超级管理员角色id
    public static Long sysSuperAdminRoleId;

    @Value("${sys.super.admin.roleId}")
    public void setSysSuperAdminRoleId(Long sysSuperAdminRoleId) {
        SysAdminUtils.sysSuperAdminRoleId = sysSuperAdminRoleId;
    }

    /**
     * 获取当前登录用户的信息
     */
    public static SysUserVO getSysUserVO(){
        return (SysUserVO) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * 获取当前登录用户的用户id
     */
    public static Long getSysUserId(){
        if (getSysUserVO() == null){
            return null;
        }
        return getSysUserVO().getSysUserId();
    }

    /**
     * 检测角色id列表是否包含超级管理员
     */
    public static boolean isHaveSuperAdmin(List<Long> roleIdList){
        return roleIdList.contains(sysSuperAdminRoleId);
    }

    /**
     * 检测当前用户是否是超级管理员
     */
    public static boolean isSuperAdmin(){
        return getSysUserVO().getRoleIdList().contains(sysSuperAdminRoleId);
    }

    /**
     * 检查角色是否越权，如果越权直接抛异常
     */
    public static void checkSuperAdmin(){
        if(!isSuperAdmin()){
            throw new MyException(ResponseEnums.NO_AUTH.getCode(), "当前操作需要超级管理员");
        }
    }

}
