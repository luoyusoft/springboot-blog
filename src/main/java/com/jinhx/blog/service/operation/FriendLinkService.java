package com.jinhx.blog.service.operation;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.operation.FriendLink;
import com.jinhx.blog.entity.operation.vo.HomeFriendLinkInfoVO;

import java.util.List;

/**
 * FriendLinkService
 *
 * @author jinhx
 * @since 2019-02-14
 */
public interface FriendLinkService {

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    HomeFriendLinkInfoVO selectHommeFriendLinkInfoVO();

    /**
     * 分页查询友链列表
     *
     * @param page page
     * @param limit limit
     * @param title title
     * @return 友链列表
     */
     PageData<FriendLink> selectPage(Integer page, Integer limit, String title);

    /**
     * 根据friendLinkId查询友链
     *
     * @param friendLinkId friendLinkId
     * @return 友链
     */
    FriendLink selectFriendLinkById(Long friendLinkId);

    /**
     * 新增友链
     *
     * @param friendLink friendLink
     */
    void insertFriendLink(FriendLink friendLink);

    /**
     * 根据friendLinkId更新友链
     *
     * @param friendLink friendLink
     */
    void updateFriendLinkById(FriendLink friendLink);

    /**
     * 批量根据friendLinkId删除友链
     *
     * @param friendLinkIds friendLinkIds
     */
    void deleteFriendLinksById(List<Long> friendLinkIds);

    /********************** portal ********************************/

    /**
     * 查询友链列表
     *
     * @return 友链列表
     */
    List<FriendLink> selectPortalFriendLinks();

}
