package com.jinhx.blog.service.operation.impl;

import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.operation.FriendLink;
import com.jinhx.blog.entity.operation.vo.HomeFriendLinkInfoVO;
import com.jinhx.blog.service.operation.FriendLinkMapperService;
import com.jinhx.blog.service.operation.FriendLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FriendLinkServiceImpl
 *
 * @author jinhx
 * @since 2019-02-14
 */
@CacheConfig(cacheNames = RedisKeyConstants.FRIENDLINKS)
@Service
public class FriendLinkServiceImpl implements FriendLinkService {

    @Autowired
    private FriendLinkMapperService friendLinkMapperService;

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    @Override
    public HomeFriendLinkInfoVO selectHommeFriendLinkInfoVO() {
        return friendLinkMapperService.selectHommeFriendLinkInfoVO();
    }

    /**
     * 分页查询友链列表
     *
     * @param page page
     * @param limit limit
     * @param title title
     * @return 友链列表
     */
    @Override
    public PageData<FriendLink> selectPage(Integer page, Integer limit, String title) {
        return friendLinkMapperService.selectPage(page, limit, title);
    }

    /**
     * 根据friendLinkId查询友链
     *
     * @param friendLinkId friendLinkId
     * @return 友链
     */
    @Override
    public FriendLink selectFriendLinkById(Long friendLinkId) {
        return friendLinkMapperService.selectFriendLinkById(friendLinkId);
    }

    /**
     * 新增友链
     *
     * @param friendLink friendLink
     */
    @Override
    public void insertFriendLink(FriendLink friendLink) {
        friendLinkMapperService.insertFriendLink(friendLink);
    }

    /**
     * 根据friendLinkId更新友链
     *
     * @param friendLink friendLink
     */
    @Override
    public void updateFriendLinkById(FriendLink friendLink) {
        friendLinkMapperService.updateFriendLinkById(friendLink);
    }

    /**
     * 批量根据friendLinkId删除友链
     *
     * @param friendLinkIds friendLinkIds
     */
    @Override
    public void deleteFriendLinksById(List<Long> friendLinkIds) {
        friendLinkMapperService.deleteFriendLinksById(friendLinkIds);
    }

    /********************** portal ********************************/

    /**
     * 查询友链列表
     *
     * @return 友链列表
     */
    @Cacheable
    @Override
    public List<FriendLink> selectPortalFriendLinks() {
        return friendLinkMapperService.selectPortalFriendLinks();
    }

}
