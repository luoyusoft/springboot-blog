package com.jinhx.blog.service.operation;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.common.util.PageUtils;
import com.jinhx.blog.entity.operation.FriendLink;
import com.jinhx.blog.entity.operation.vo.HomeFriendLinkInfoVO;

import java.util.List;

/**
 * FriendLinkService
 *
 * @author jinhx
 * @since 2019-02-14
 */
public interface FriendLinkService extends IService<FriendLink> {

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    HomeFriendLinkInfoVO getHommeFriendLinkInfoVO();

    /**
     * 分页查询
     *
     * @param page page
     * @param limit limit
     * @param title title
     * @return PageUtils
     */
     PageUtils queryPage(Integer page, Integer limit, String title);

    /**
     * 判断上传文件下是否有友链
     *
     * @param url url
     * @return 是否有友链
     */
    Boolean checkByFile(String url);

    /********************** portal ********************************/

    /**
     * 获取友链列表
     *
     * @return 友链列表
     */
    List<FriendLink> listFriendLinks();

}
