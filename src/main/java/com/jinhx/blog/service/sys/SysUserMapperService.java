package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.SysAdminUtils;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.sys.SysUser;
import com.jinhx.blog.entity.sys.dto.SysUserDTO;
import com.jinhx.blog.mapper.sys.SysUserMapper;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * SysUserMapperService
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public class SysUserMapperService extends ServiceImpl<SysUserMapper, SysUser> {

    @Autowired
    private SysUserRoleMapperService sysUserRoleMapperService;

    /**
     * 查询用户菜单列表
     *
     * @param userId 用户id
     * @return 用户菜单列表
     */
    public List<Integer> queryAllMenuId(Integer userId) {
        return baseMapper.queryAllMenuId(userId);
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
    public PageData queryPage(Integer page, Integer limit, String username, Integer id) {
        IPage<SysUser> sysUserIPage = baseMapper.selectPage(new QueryPage<SysUser>(page, limit).getPage(),
                new LambdaQueryWrapper<SysUser>()
                        .eq(id != null, SysUser::getId, id)
                        .like(StringUtils.isNotBlank(username), SysUser::getUsername, username));

        List<SysUserDTO> sysUserDTOList = new ArrayList<>();
        sysUserIPage.getRecords().forEach(item -> {
            // 如果当前用户不是超级管理员，则不展示超级管理员
            if(!SysAdminUtils.isSuperAdmin() && SysAdminUtils.isHaveSuperAdmin(sysUserRoleMapperService.getRoleIdListByUserId(item.getId()))){
                return;
            }
            SysUserDTO sysUserDTO = new SysUserDTO();
            BeanUtils.copyProperties(item, sysUserDTO);
            sysUserDTO.setRoleNameStr(String.join(",", sysUserRoleMapperService.queryRoleNameList(item.getId())));
            sysUserDTOList.add(sysUserDTO);
        });

        IPage<SysUserDTO> sysUserDTOPage = new Page<>();
        BeanUtils.copyProperties(sysUserIPage, sysUserDTOPage);
        sysUserDTOPage.setRecords(sysUserDTOList);

        return new PageData(sysUserDTOPage);
    }

    /**
     * 更新密码
     *
     * @param userId 用户id
     * @param password 旧密码
     * @param newPassword 新密码
     * @return 更新结果
     */
    public boolean updatePassword(Integer userId, String password, String newPassword) {
        // 如果不是本人操作，且操作用户为超级管理员，则需要当前用户拥有超级管理员权限
        if (!userId.equals(SysAdminUtils.getUserId())){
            List<Integer> roleIdList = sysUserRoleMapperService.getRoleIdListByUserId(userId);
            if (!CollectionUtils.isEmpty(roleIdList) && SysAdminUtils.isHaveSuperAdmin(roleIdList)){
                SysAdminUtils.checkSuperAdmin();
            }
        }

        SysUser sysUser = new SysUser();
        sysUser.setPassword(newPassword);
        return update(sysUser, new UpdateWrapper<SysUser>().lambda()
                .eq(SysUser::getId,userId).eq(SysUser::getPassword,password));
    }

    /**
     * 重置密码
     *
     * @param userId 用户id
     * @param password 新密码
     * @return 重置结果
     */
    public boolean resetPassword(Integer userId, String password) {
        // 如果不是本人操作，且操作用户为超级管理员，则需要当前用户拥有超级管理员权限
        if (!userId.equals(SysAdminUtils.getUserId())){
            List<Integer> roleIdList = sysUserRoleMapperService.getRoleIdListByUserId(userId);
            if (!CollectionUtils.isEmpty(roleIdList) && SysAdminUtils.isHaveSuperAdmin(roleIdList)){
                SysAdminUtils.checkSuperAdmin();
            }
        }

        SysUser oldSysUser = baseMapper.selectById(userId);
        if (oldSysUser == null){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "用户不存在");
        }

        SysUser sysUser = new SysUser();
        sysUser.setPassword(new Sha256Hash(password, oldSysUser.getSalt()).toHex());
        return update(sysUser, new UpdateWrapper<SysUser>().lambda().eq(SysUser::getId, userId));
    }

    /**
     * 新增用户信息
     *
     * @param sysUserDTO 用户信息
     * @return 新增结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean insertSysUser(SysUserDTO sysUserDTO) {
        // 如果新增超级管理员，需要当前用户拥有超级管理员权限
        if (!CollectionUtils.isEmpty(sysUserDTO.getRoleIdList()) && SysAdminUtils.isHaveSuperAdmin(sysUserDTO.getRoleIdList())){
            SysAdminUtils.checkSuperAdmin();
        }

        if (baseMapper.countSysUserByUsername(sysUserDTO.getUsername()) > 0){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该用户名已存在");
        }

        // sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        sysUserDTO.setPassword(new Sha256Hash(sysUserDTO.getPassword(), salt).toHex());
        sysUserDTO.setSalt(salt);
        baseMapper.insert(sysUserDTO);

        // 保存用户与角色关系
        sysUserRoleMapperService.saveOrUpdate(sysUserDTO.getId(), sysUserDTO.getRoleIdList());
        return true;
    }

    /**
     * 根据用户id更新用户信息
     *
     * @param sysUserDTO 用户信息
     * @return 更新结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSysUserById(SysUserDTO sysUserDTO) {
        // 如果不是本人操作，且操作用户为超级管理员，则需要当前用户拥有超级管理员权限
        if (!sysUserDTO.getId().equals(SysAdminUtils.getUserId())){
            List<Integer> roleIdList = sysUserRoleMapperService.getRoleIdListByUserId(sysUserDTO.getId());
            if (!CollectionUtils.isEmpty(roleIdList) && SysAdminUtils.isHaveSuperAdmin(roleIdList)){
                SysAdminUtils.checkSuperAdmin();
            }
        }

        SysUser sysUser = baseMapper.selectById(sysUserDTO.getId());
        if (!sysUser.getUsername().equals(sysUserDTO.getUsername()) && baseMapper.countSysUserByUsername(sysUserDTO.getUsername()) > 0){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该用户名已存在");
        }

        if(StringUtils.isBlank(sysUserDTO.getPassword())){
            sysUserDTO.setPassword(null);
        }else{
            sysUserDTO.setPassword(new Sha256Hash(sysUserDTO.getPassword(), sysUserDTO.getSalt()).toHex());
        }
        baseMapper.updateById(sysUserDTO);

        // 保存用户与角色关系
        sysUserRoleMapperService.saveOrUpdate(sysUserDTO.getId(), sysUserDTO.getRoleIdList());
        return true;
    }

    /**
     * 根据用户id列表批量删除用户
     *
     * @param userIds 用户id列表
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatch(Integer[] userIds) {
        // 如果包括超级管理员，需要当前用户拥有超级管理员权限
        if (sysUserRoleMapperService.isHaveSuperAdmin(userIds)){
            SysAdminUtils.checkSuperAdmin();
        }

        removeByIds(Lists.newArrayList(userIds));
        // 删除用户与角色关联
        sysUserRoleMapperService.deleteBatchByUserId(userIds);

        return true;
    }

    /**
     * 根据用户id查询用户有权限所有菜单列表
     *
     * @param userId 用户id
     * @return 用户有权限所有菜单列表
     */
    public List<String> getAllPermsByUserId(Integer userId) {
        return baseMapper.getAllPermsByUserId(userId);
    }

    /**
     * 根据用户名获取SysUserDTO
     *
     * @param username 用户名
     * @return SysUserDTO
     */
    public SysUserDTO getSysUserDTOByUsername(String username) {
        SysUser sysUser = baseMapper.getSysUserByUsername(username);
        if (sysUser == null){
            return null;
        }
        SysUserDTO sysUserDTO = new SysUserDTO();
        BeanUtils.copyProperties(sysUser, sysUserDTO);
        sysUserDTO.setRoleIdList(sysUserRoleMapperService.getRoleIdListByUserId(sysUser.getId()));
        return sysUserDTO;
    }

    /**
     * 根据用户id获取SysUserDTO
     *
     * @param userId 用户id
     * @return SysUserDTO
     */
    public SysUserDTO getSysUserDTOByUserId(Integer userId) {
        SysUser sysUser = baseMapper.getSysUserByUserId(userId);
        if (sysUser == null){
            return null;
        }
        SysUserDTO sysUserDTO = new SysUserDTO();
        BeanUtils.copyProperties(sysUser, sysUserDTO);
        sysUserDTO.setRoleIdList(sysUserRoleMapperService.getRoleIdListByUserId(sysUser.getId()));
        return sysUserDTO;
    }

    /**
     * 根据用户id获取用户昵称
     *
     * @param userId 用户id
     * @return 用户昵称
     */
    public String getNicknameByUserId(Integer userId) {
        return baseMapper.getNicknameByUserId(userId);
    }

}
