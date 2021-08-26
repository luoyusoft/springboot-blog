package com.jinhx.blog.service.operation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.operation.FriendLink;
import com.jinhx.blog.entity.operation.vo.HomeFriendLinkInfoVO;
import com.jinhx.blog.mapper.operation.FriendLinkMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * FriendLinkMapperService
 *
 * @author jinhx
 * @since 2019-02-14
 */
@Service
public class FriendLinkMapperService extends ServiceImpl<FriendLinkMapper, FriendLink> {

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    public HomeFriendLinkInfoVO selectHommeFriendLinkInfoVO() {
        HomeFriendLinkInfoVO homeFriendLinkInfoVO = new HomeFriendLinkInfoVO();
        homeFriendLinkInfoVO.setCount(baseMapper.selectCount(new LambdaQueryWrapper<>()));
        return homeFriendLinkInfoVO;
    }

    /**
     * 分页查询友链列表
     *
     * @param page page
     * @param limit limit
     * @param title title
     * @return 友链列表
     */
    public PageData<FriendLink> selectPage(Integer page, Integer limit, String title) {
        return new PageData<>(baseMapper.selectPage(new QueryPage<FriendLink>(page, limit).getPage(),
                new LambdaQueryWrapper<FriendLink>().like(StringUtils.isNotEmpty(title), FriendLink::getTitle, title)));
    }

    /**
     * 根据friendLinkId查询友链
     *
     * @param friendLinkId friendLinkId
     * @return 友链
     */
    public FriendLink selectFriendLinkById(Long friendLinkId) {
        List<FriendLink> friendLinks = selectFriendLinksById(Lists.newArrayList(friendLinkId));
        if (CollectionUtils.isEmpty(friendLinks)){
            return null;
        }

        return friendLinks.get(0);
    }

    /**
     * 根据friendLinkId查询友链列表
     *
     * @param friendLinkIds friendLinkIds
     * @return 友链列表
     */
    public List<FriendLink> selectFriendLinksById(List<Long> friendLinkIds) {
        if (CollectionUtils.isEmpty(friendLinkIds)){
            return Lists.newArrayList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<FriendLink>().in(FriendLink::getFriendLinkId, friendLinkIds));
    }

    /**
     * 新增友链
     *
     * @param friendLink friendLink
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertFriendLink(FriendLink friendLink) {
        insertFriendLinks(Lists.newArrayList(friendLink));
    }

    /**
     * 批量新增友链
     *
     * @param friendLinks friendLinks
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertFriendLinks(List<FriendLink> friendLinks) {
        if (CollectionUtils.isNotEmpty(friendLinks)){
            if (friendLinks.stream().mapToInt(item -> baseMapper.insert(item)).sum() != friendLinks.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

    /**
     * 根据friendLinkId更新友链
     *
     * @param friendLink friendLink
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFriendLinkById(FriendLink friendLink) {
        updateFriendLinksById(Lists.newArrayList(friendLink));
    }

    /**
     * 批量根据friendLinkId更新友链
     *
     * @param friendLinks friendLinks
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFriendLinksById(List<FriendLink> friendLinks) {
        if (CollectionUtils.isNotEmpty(friendLinks)){
            if (friendLinks.stream().mapToInt(item -> baseMapper.updateById(item)).sum() != friendLinks.size()){
                throw new MyException(ResponseEnums.UPDATE_FAILR);
            }
        }
    }

    /**
     * 批量根据friendLinkId删除友链
     *
     * @param friendLinkIds friendLinkIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFriendLinksById(List<Long> friendLinkIds) {
        if (CollectionUtils.isNotEmpty(friendLinkIds)){
            if (baseMapper.deleteBatchIds(friendLinkIds) != friendLinkIds.size()){
                throw new MyException(ResponseEnums.DELETE_FAIL);
            }
        }
    }

    /********************** portal ********************************/

    /**
     * 获取友链列表
     *
     * @return 友链列表
     */
    public List<FriendLink> selectPortalFriendLinks() {
        return baseMapper.selectList(new LambdaQueryWrapper<>());
    }

}
