package com.jinhx.blog.controller.sys;

import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.MyAssert;
import com.jinhx.blog.common.util.SysAdminUtils;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.InsertGroup;
import com.jinhx.blog.common.validator.group.UpdateGroup;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.sys.SysUser;
import com.jinhx.blog.entity.sys.vo.PasswordVO;
import com.jinhx.blog.entity.sys.vo.SysUserVO;
import com.jinhx.blog.service.sys.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SysUserController
 *
 * @author jinhx
 * @since 2018-10-08
 */
@RestController
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 分页查询用户列表
     *
     * @param page 页码
     * @param limit 页数
     * @param username 用户名
     * @param sysUserId 用户id
     * @return 用户列表
     */
    @GetMapping("/manage/sys/user/list")
    @RequiresPermissions("sys:user:list")
    public Response<PageData<SysUserVO>> selectPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit,
                                                    @RequestParam("username") String username, @RequestParam("id") Long sysUserId){
        return Response.success(sysUserService.selectPage(page, limit, username, sysUserId));
    }

    /**
     * 获取登录用户信息
     *
     * @return 登录用户信息
     */
    @GetMapping("/manage/sys/user/info")
    public Response<SysUserVO> getLoginSysUserVO(){
        return Response.success(SysAdminUtils.getSysUserVO());
    }

    /**
     * 根据用户id重置密码
     *
     * @param passwordVO 密码信息
     * @return 重置结果
     */
    @PutMapping("/manage/sys/user/resetPassword")
    public Response<Void> resetPasswordById(@RequestBody PasswordVO passwordVO){
        ValidatorUtils.validateEntity(passwordVO, UpdateGroup.class);
        sysUserService.resetPasswordById(passwordVO.getSysUserId(), passwordVO.getPassword());
        return Response.success();
    }

    /**
     * 新增用户
     *
     * @param sysUserVO 用户信息
     * @return 初始密码
     */
    @PostMapping("/manage/sys/user/save")
    @RequiresPermissions("sys:user:save")
    public Response<String> insertSysUser(@RequestBody SysUserVO sysUserVO){
        ValidatorUtils.validateEntity(sysUserVO, InsertGroup.class);
        if(StringUtils.isEmpty(sysUserVO.getProfile())){
            sysUserVO.setProfile(SysUser.sysUserDefaultProfile);
        }

        return Response.success(sysUserService.insertSysUser(sysUserVO));
    }

    /**
     * 根据sysUserId更新用户
     *
     * @param sysUserVO sysUserVO
     * @return 更新结果
     */
    @PutMapping("/manage/sys/user/update")
    @RequiresPermissions("sys:user:update")
    public Response<Void> updateSysUserById(@RequestBody SysUserVO sysUserVO){
        ValidatorUtils.validateEntity(sysUserVO, UpdateGroup.class);
        sysUserService.updateSysUserById(sysUserVO);
        return Response.success();
    }

    /**
     * 批量根据sysUserId删除用户
     *
     * @param sysUserIds sysUserIds
     * @return 删除结果
     */
    @PostMapping("/manage/sys/user/delete")
    @RequiresPermissions("sys:user:delete")
    public Response<Void> deleteSysUsersById(@RequestBody List<Long> sysUserIds){
        MyAssert.sizeBetween(sysUserIds, 1, 100, "sysUserIds");
        if(sysUserIds.contains(SysAdminUtils.getSysUserId())){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "当前登录用户不能删除");
        }

        sysUserService.deleteSysUsersById(sysUserIds);
        return Response.success();
    }

    /**
     * 根据用户id查询SysUserVO
     *
     * @param sysUserId 用户id
     * @return SysUserVO
     */
    @GetMapping("/manage/sys/user/info/{userId}")
    @RequiresPermissions("sys:user:info")
    public Response<SysUserVO> selectSysUserVOById(@PathVariable("userId") Long sysUserId){
        return Response.success(sysUserService.selectSysUserVOById(sysUserId));
    }

}
