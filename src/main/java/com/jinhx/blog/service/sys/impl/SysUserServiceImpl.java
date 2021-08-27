package com.jinhx.blog.service.sys.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.SysAdminUtils;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.sys.SysUser;
import com.jinhx.blog.entity.sys.vo.SysUserVO;
import com.jinhx.blog.service.sys.SysUserMapperService;
import com.jinhx.blog.service.sys.SysUserRoleMapperService;
import com.jinhx.blog.service.sys.SysUserService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * SysUserServiceImpl
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserRoleMapperService sysUserRoleMapperService;

    @Autowired
    private SysUserMapperService sysUserMapperService;

    /**
     * 分页查询用户列表
     *
     * @param page 页码
     * @param limit 页数
     * @param username 用户名
     * @param sysUserId 用户id
     * @return 用户列表
     */
    @Override
    public PageData<SysUserVO> selectPage(Integer page, Integer limit, String username, Long sysUserId) {
        IPage<SysUser> sysUserIPage = sysUserMapperService.selectPage(page, limit, username, sysUserId);

        if (CollectionUtils.isEmpty(sysUserIPage.getRecords())){
            return new PageData<>();
        }

        List<SysUserVO> sysUserVOList = new ArrayList<>();
        sysUserIPage.getRecords().forEach(item -> {
            // 如果当前用户不是超级管理员，则不展示超级管理员
            if(!SysAdminUtils.isSuperAdmin() && SysAdminUtils.isHaveSuperAdmin(sysUserRoleMapperService.selectSysRoleIdsBySysUserId(item.getSysUserId()))){
                return;
            }
            SysUserVO sysUserVO = new SysUserVO();
            BeanUtils.copyProperties(item, sysUserVO);
            sysUserVO.setRoleNameStr(String.join(",", sysUserRoleMapperService.selectRoleNamesBySysUserId(item.getSysUserId())));
            sysUserVOList.add(sysUserVO);
        });

        IPage<SysUserVO> sysUserDTOPage = new Page<>();
        BeanUtils.copyProperties(sysUserIPage, sysUserDTOPage);
        sysUserDTOPage.setRecords(sysUserVOList);

        return new PageData<>(sysUserDTOPage);
    }

    /**
     * 根据用户id重置密码
     *
     * @param sysUserId 用户id
     * @param newPassword 新密码
     */
    @Override
    public void resetPasswordById(Long sysUserId, String newPassword) {
        // 如果不是本人操作，且操作用户为超级管理员，则需要当前用户拥有超级管理员权限
        if (!sysUserId.equals(SysAdminUtils.getSysUserId())){
            List<Long> roleIdList = sysUserRoleMapperService.selectSysRoleIdsBySysUserId(sysUserId);
            if (CollectionUtils.isNotEmpty(roleIdList) && SysAdminUtils.isHaveSuperAdmin(roleIdList)){
                SysAdminUtils.checkSuperAdmin();
            }
        }

        SysUser oldSysUser = sysUserMapperService.selectSysUserById(sysUserId);
        if (Objects.isNull(oldSysUser)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "用户不存在");
        }

        SysUser sysUser = new SysUser();
        sysUser.setSysUserId(sysUserId);
        sysUser.setPassword(new Sha256Hash(newPassword, oldSysUser.getSalt()).toHex());
        sysUserMapperService.updateSysUserById(sysUser);
    }

    /**
     * 新增用户
     *
     * @param sysUserVO 用户信息
     * @return 初始密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String insertSysUser(SysUserVO sysUserVO) {
        // 如果新增超级管理员，需要当前用户拥有超级管理员权限
        if (CollectionUtils.isNotEmpty(sysUserVO.getRoleIdList()) && SysAdminUtils.isHaveSuperAdmin(sysUserVO.getRoleIdList())){
            SysAdminUtils.checkSuperAdmin();
        }

        if (sysUserMapperService.selectSysUserCountByUsername(sysUserVO.getUsername()) > 0){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该用户名已存在");
        }

        // sha256加密，随机6位的初始密码
        String password = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        String salt = RandomStringUtils.randomAlphanumeric(20);
        sysUserVO.setPassword(new Sha256Hash(password, salt).toHex());
        sysUserVO.setSalt(salt);
        sysUserMapperService.insertSysUser(sysUserVO);

        // 保存用户与角色关系
        sysUserRoleMapperService.deleteOldAndInsertNewSysUserRole(sysUserVO.getSysUserId(), sysUserVO.getRoleIdList());
        return password;
    }

    /**
     * 根据sysUserId更新用户
     *
     * @param sysUserVO sysUserVO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSysUserById(SysUserVO sysUserVO) {
        // 如果不是本人操作，且操作用户为超级管理员，则需要当前用户拥有超级管理员权限
        if (!sysUserVO.getSysUserId().equals(SysAdminUtils.getSysUserId())){
            List<Long> roleIdList = sysUserRoleMapperService.selectSysRoleIdsBySysUserId(sysUserVO.getSysUserId());
            if (CollectionUtils.isNotEmpty(roleIdList) && SysAdminUtils.isHaveSuperAdmin(roleIdList)){
                SysAdminUtils.checkSuperAdmin();
            }
        }

        SysUser sysUser = sysUserMapperService.selectSysUserById(sysUserVO.getSysUserId());
        if (!sysUser.getUsername().equals(sysUserVO.getUsername()) && sysUserMapperService.selectSysUserCountByUsername(sysUserVO.getUsername()) > 0){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该用户名已存在");
        }

        if(StringUtils.isBlank(sysUserVO.getPassword())){
            sysUserVO.setPassword(null);
        }else{
            sysUserVO.setPassword(new Sha256Hash(sysUserVO.getPassword(), sysUserVO.getSalt()).toHex());
        }

        sysUserMapperService.updateSysUserById(sysUserVO);

        // 保存用户与角色关系
        sysUserRoleMapperService.deleteOldAndInsertNewSysUserRole(sysUserVO.getSysUserId(), sysUserVO.getRoleIdList());
    }

    /**
     * 批量根据sysUserId删除用户
     *
     * @param sysUserIds sysUserIds
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysUsersById(List<Long> sysUserIds) {
        // 如果包括超级管理员，需要当前用户拥有超级管理员权限
        if (sysUserRoleMapperService.selectSysUserRoleCountBySysUserIdAndSysRoleId(sysUserIds, SysAdminUtils.sysSuperAdminRoleId) > 0){
            SysAdminUtils.checkSuperAdmin();
        }

        sysUserMapperService.deleteSysUsersById(sysUserIds);
        // 删除用户与角色关联
        sysUserRoleMapperService.deleteSysUserRolesBySysUserId(sysUserIds);
    }

    /**
     * 根据用户名获取SysUserDTO
     *
     * @param username 用户名
     * @return SysUserDTO
     */
    @Override
    public SysUserVO selectSysUserVOByUsername(String username) {
        SysUser sysUser = sysUserMapperService.selectSysUserByUsername(username);
        if (Objects.isNull(sysUser)){
            return null;
        }
        SysUserVO sysUserVO = new SysUserVO();
        BeanUtils.copyProperties(sysUser, sysUserVO);
        sysUserVO.setRoleIdList(sysUserRoleMapperService.selectSysRoleIdsBySysUserId(sysUser.getSysUserId()));
        return sysUserVO;
    }

    /**
     * 根据用户id查询SysUserVO
     *
     * @param sysUserId 用户id
     * @return SysUserVO
     */
    @Override
    public SysUserVO selectSysUserVOById(Long sysUserId) {
        SysUser sysUser = sysUserMapperService.selectSysUserById(sysUserId);
        if (Objects.isNull(sysUser)){
            return null;
        }

        SysUserVO sysUserVO = new SysUserVO();
        BeanUtils.copyProperties(sysUser, sysUserVO);
        sysUserVO.setRoleIdList(sysUserRoleMapperService.selectSysRoleIdsBySysUserId(sysUser.getSysUserId()));
        return sysUserVO;
    }

}
