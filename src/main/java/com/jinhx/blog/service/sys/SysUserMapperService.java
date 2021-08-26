package com.jinhx.blog.service.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.sys.SysUser;
import com.jinhx.blog.mapper.sys.SysUserMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * SysUserMapperService
 *
 * @author jinhx
 * @since 2018-10-22
 */
@Service
public class SysUserMapperService extends ServiceImpl<SysUserMapper, SysUser> {

    /**
     * 根据用户id查询用户菜单列表
     *
     * @param sysUserId 用户id
     * @return 用户菜单列表
     */
    public List<Long> selectSysMenuIdsBySysUserId(Long sysUserId) {
        return baseMapper.selectSysMenuIdsBySysUserId(sysUserId);
    }

    /**
     * 分页查询用户列表
     *
     * @param page 页码
     * @param limit 页数
     * @param username 用户名
     * @param sysUserId 用户id
     * @return 用户列表
     */
    public IPage<SysUser> selectPage(Integer page, Integer limit, String username, Long sysUserId) {
        return baseMapper.selectPage(new QueryPage<SysUser>(page, limit).getPage(),
                new LambdaQueryWrapper<SysUser>()
                        .eq(Objects.nonNull(sysUserId), SysUser::getSysUserId, sysUserId)
                        .like(StringUtils.isNotBlank(username), SysUser::getUsername, username));
    }

    /**
     * 批量根据sysUserId删除用户
     *
     * @param sysUserIds sysUserIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSysUsersById(List<Long> sysUserIds) {
        if (CollectionUtils.isNotEmpty(sysUserIds)){
            if (baseMapper.deleteBatchIds(sysUserIds) != sysUserIds.size()){
                throw new MyException(ResponseEnums.DELETE_FAIL);
            }
        }
    }

    /**
     * 根据用户id查询用户有权限所有菜单列表
     *
     * @param sysUserId 用户id
     * @return 用户有权限所有菜单列表
     */
    public List<String> selectAllPermsBySysUserId(Long sysUserId) {
        return baseMapper.selectAllPermsBySysUserId(sysUserId);
    }

    /**
     * 根据用户名查询用户
     *
     * @param username username
     * @return 用户
     */
    public SysUser selectSysUserByUsername(String username) {
        List<SysUser> sysUsers = baseMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .last("limit 1"));

        if (CollectionUtils.isEmpty(sysUsers)) {
            return null;
        }

        return sysUsers.get(0);
    }

    /**
     * 根据sysUserId查询用户
     *
     * @param sysUserId sysUserId
     * @return 用户
     */
    public SysUser selectSysUserById(Long sysUserId) {
        List<SysUser> sysUsers = selectSysUsersById(Lists.newArrayList(sysUserId));
        if (CollectionUtils.isEmpty(sysUsers)){
            return null;
        }

        return sysUsers.get(0);
    }

    /**
     * 根据sysUserId查询用户列表
     *
     * @param sysUserIds sysUserIds
     * @return 用户列表
     */
    public List<SysUser> selectSysUsersById(List<Long> sysUserIds) {
        if (CollectionUtils.isEmpty(sysUserIds)){
            return Lists.newArrayList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getSysUserId, sysUserIds));
    }

    /**
     * 根据用户名查询用户数量
     *
     * @param username username
     * @return 用户数量
     */
    public Integer selectSysUserCountByUsername(String username) {
        return baseMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
    }

    /**
     * 根据用户id查询用户昵称
     *
     * @param sysUserId sysUserId
     * @return 用户昵称
     */
    public String selectNicknameBySysUserId(Long sysUserId) {
        SysUser sysUser = selectSysUserById(sysUserId);

        if (Objects.isNull(sysUser)) {
            return null;
        }

        return sysUser.getNickname();
    }

    /**
     * 根据sysUserId更新用户
     *
     * @param sysUser sysUser
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSysUserById(SysUser sysUser) {
        updateSysUsersById(Lists.newArrayList(sysUser));
    }

    /**
     * 批量根据sysUserId更新用户
     *
     * @param sysUsers sysUsers
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSysUsersById(List<SysUser> sysUsers) {
        if (CollectionUtils.isNotEmpty(sysUsers)){
            if (sysUsers.stream().mapToInt(item -> baseMapper.updateById(item)).sum() != sysUsers.size()){
                throw new MyException(ResponseEnums.UPDATE_FAILR);
            }
        }
    }

    /**
     * 新增用户
     *
     * @param sysUser sysUser
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertSysUser(SysUser sysUser) {
        insertSysUsers(Lists.newArrayList(sysUser));
    }

    /**
     * 批量新增用户
     *
     * @param sysUsers sysUsers
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertSysUsers(List<SysUser> sysUsers) {
        if (CollectionUtils.isNotEmpty(sysUsers)){
            if (sysUsers.stream().mapToInt(item -> baseMapper.insert(item)).sum() != sysUsers.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

}
