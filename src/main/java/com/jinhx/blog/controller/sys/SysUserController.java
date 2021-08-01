package com.jinhx.blog.controller.sys;

import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.SysAdminUtils;
import com.jinhx.blog.common.validator.ValidatorUtils;
import com.jinhx.blog.common.validator.group.AddGroup;
import com.jinhx.blog.common.validator.group.UpdateGroup;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.sys.SysUser;
import com.jinhx.blog.entity.sys.dto.SysUserDTO;
import com.jinhx.blog.entity.sys.vo.PasswordVO;
import com.jinhx.blog.service.sys.SysUserService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 获取登录用户信息
     *
     * @return 登录用户信息
     */
    @GetMapping("/manage/sys/user/info")
    public Response info(){
        return Response.success(SysAdminUtils.getUserDTO());
    }

    /**
     * 分页查询用户信息列表
     *
     * @param page 页码
     * @param limit 页数
     * @param username 用户名
     * @param id 用户id
     * @return 用户信息列表
     */
    @GetMapping("/manage/sys/user/list")
    @RequiresPermissions("sys:user:list")
    public Response listSysUsers(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit,
                                 @RequestParam("username") String username, @RequestParam("id") Integer id){
        return Response.success(sysUserService.queryPage(page, limit, username, id));
    }

    /**
     * 更新密码
     *
     * @param passwordVO 密码信息
     * @return 更新结果
     */
    @PutMapping("/manage/sys/user/password")
    public Response password(@RequestBody PasswordVO passwordVO){
        if(StringUtils.isEmpty(passwordVO.getNewPassword())) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "新密码不能为空");
        }
        if(passwordVO.getNewPassword().length() < 6) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "新密码长度不能低于6位");
        }

        // sha256加密
        String password = new Sha256Hash(passwordVO.getPassword(), SysAdminUtils.getUserDTO().getSalt()).toHex();
        // sha256加密
        String newPassword = new Sha256Hash(passwordVO.getNewPassword(), SysAdminUtils.getUserDTO().getSalt()).toHex();

        boolean flag = sysUserService.updatePassword(SysAdminUtils.getUserId(), password, newPassword);
        if(!flag){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "原密码不正确");
        }

        return Response.success();
    }

    /**
     * 重置密码
     *
     * @param passwordVO 密码信息
     * @return 重置结果
     */
    @PutMapping("/manage/sys/user/resetPassword")
    public Response resetPassword(@RequestBody PasswordVO passwordVO){
        if(StringUtils.isEmpty(passwordVO.getPassword())) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "新密码不能为空");
        }
        if(passwordVO.getPassword().length() < 6) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "新密码长度不能低于6位");
        }

        // sha256加密
        boolean flag = sysUserService.resetPassword(passwordVO.getId(), passwordVO.getPassword());
        if(!flag){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "重置失败");
        }

        return Response.success();
    }

    /**
     * 新增用户信息
     *
     * @param sysUserDTO 用户信息
     * @return 新增结果
     */
    @PostMapping("/manage/sys/user/save")
    @RequiresPermissions("sys:user:save")
    public Response insertSysUser(@RequestBody SysUserDTO sysUserDTO){
        ValidatorUtils.validateEntity(sysUserDTO, AddGroup.class);

        if(StringUtils.isEmpty(sysUserDTO.getProfile())){
            sysUserDTO.setProfile(SysUser.sysUserDefaultProfile);
        }
        sysUserService.insertSysUser(sysUserDTO);

        return Response.success();
    }

    /**
     * 根据用户id更新用户信息
     *
     * @param sysUserDTO 用户信息
     * @return 更新结果
     */
    @PutMapping("/manage/sys/user/update")
    @RequiresPermissions("sys:user:update")
    public Response updateSysUserById(@RequestBody SysUserDTO sysUserDTO){
        ValidatorUtils.validateEntity(sysUserDTO, UpdateGroup.class);

        sysUserService.updateSysUserById(sysUserDTO);

        return Response.success();
    }

    /**
     * 根据用户id列表批量删除用户
     *
     * @param userIds 用户id列表
     */
    @PostMapping("/manage/sys/user/delete")
    @RequiresPermissions("sys:user:delete")
    public Response delete(@RequestBody Integer[] userIds){
        if (userIds == null || userIds.length < 1){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "userIds不能为空");
        }

        if (userIds.length > 100){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "userIds不能超过100个");
        }

        if(ArrayUtils.contains(userIds, SysAdminUtils.getUserId())){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "当前登录用户不能删除");
        }

        sysUserService.deleteBatch(userIds);

        return Response.success();
    }

    /**
     * 根据用户id获取SysUserDTO
     *
     * @param userId 用户id
     * @return SysUserDTO
     */
    @GetMapping("/manage/sys/user/info/{userId}")
    @RequiresPermissions("sys:user:info")
    public Response info(@PathVariable("userId") Integer userId){
        return Response.success(sysUserService.getSysUserDTOByUserId(userId));
    }

}
