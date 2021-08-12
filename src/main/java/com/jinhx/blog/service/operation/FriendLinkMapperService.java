package com.jinhx.blog.service.operation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.operation.FriendLink;
import com.jinhx.blog.entity.operation.vo.HomeFriendLinkInfoVO;
import com.jinhx.blog.mapper.operation.FriendLinkMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FriendLinkMapperService
 *
 * @author jinhx
 * @since 2019-02-14
 */
@CacheConfig(cacheNames = RedisKeyConstants.FRIENDLINKS)
@Service
@Slf4j
public class FriendLinkMapperService extends ServiceImpl<FriendLinkMapper, FriendLink> {

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    public HomeFriendLinkInfoVO getHommeFriendLinkInfoVO() {
        HomeFriendLinkInfoVO homeFriendLinkInfoVO = new HomeFriendLinkInfoVO();
        homeFriendLinkInfoVO.setCount(baseMapper.selectCount(new LambdaQueryWrapper<>()));
        return homeFriendLinkInfoVO;
    }

    /**
     * 分页查询
     *
     * @param page page
     * @param limit limit
     * @param title title
     * @return PageUtils
     */
    public PageData queryPage(Integer page, Integer limit, String title) {
        IPage<FriendLink> friendLinkIPage = baseMapper.selectPage(new QueryPage<FriendLink>(page, limit).getPage(),
                new LambdaQueryWrapper<FriendLink>().like(StringUtils.isNotEmpty(title), FriendLink::getTitle,title));
        return new PageData(friendLinkIPage);
    }

    /**
     * 判断上传文件下是否有友链
     *
     * @param url url
     * @return 是否有友链
     */
    public Boolean checkByFile(String url) {
        return baseMapper.selectCount(new LambdaQueryWrapper<FriendLink>()
                .eq(FriendLink::getAvatar, url)) > 0;
    }

    /********************** portal ********************************/

    /**
     * 获取友链列表
     *
     * @return 友链列表
     */
    @Cacheable
    public List<FriendLink> listFriendLinks() {
        return baseMapper.selectList(new LambdaQueryWrapper<>());
    }

}
