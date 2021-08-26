package com.jinhx.blog.service.messagewall;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.messagewall.MessageWall;
import com.jinhx.blog.entity.messagewall.vo.HomeMessageWallInfoVO;
import com.jinhx.blog.entity.messagewall.vo.MessageWallListVO;

import java.util.List;

/**
 * MessageWallService
 *
 * @author jinhx
 * @since 2021-04-11
 */
public interface MessageWallService {

    /**
     * 查询首页信息
     *
     * @return 首页信息
     */
    HomeMessageWallInfoVO selectHomeMessageWallInfoVO();

    /**
     * 分页查询留言列表
     *
     * @param page 页码
     * @param limit 页数
     * @param name 昵称
     * @param floorNum 楼层数
     * @return 留言列表
     */
    PageData<MessageWall> selectMessageWallPage(Integer page, Integer limit, String name, Integer floorNum);

    /**
     * 新增留言
     *
     * @param messageWall 留言信息
     */
    void insertMessageWall(MessageWall messageWall);

    /**
     * 批量根据messageWallId删除留言
     *
     * @param messageWallIds messageWallIds
     */
    void deleteMessageWallsById(List<Long> messageWallIds);

    /********************** portal ********************************/

    /**
     * 新增留言
     *
     * @param messageWall 留言信息
     */
    void insertPortalMessageWall(MessageWall messageWall);

    /**
     * 按楼层分页查询留言列表
     *
     * @param page 页码
     * @param limit 页数
     * @return 留言列表
     */
    MessageWallListVO selectPortalMessageWallPage(Integer page, Integer limit);

}
