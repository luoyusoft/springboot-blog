package com.jinhx.blog.service.operation;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.operation.Top;
import com.jinhx.blog.entity.operation.vo.TopVO;

import java.util.List;

/**
 * TopService
 *
 * @author jinhx
 * @since 2019-02-22
 */
public interface TopService {

    /**
     * 分页查询置顶列表
     *
     * @param page page
     * @param limit limit
     * @return 置顶列表
     */
     PageData<TopVO> selectPage(Integer page, Integer limit);

    /**
     * 根据模块，标题查询置顶列表
     *
     * @param module module
     * @param title title
     * @return 置顶列表
     */
    List<TopVO> selectTopVOsByModuleAndTitle(Integer module, String title);

    /**
     * 根据topId查询置顶
     *
     * @param topId topId
     * @return 置顶
     */
    TopVO selectTopVOById(Long topId);

    /**
     * 新增置顶
     *
     * @param top top
     */
    void insertTop(Top top);

    /**
     * 根据topId更新置顶
     *
     * @param top top
     */
    void updateTopById(Top top);

    /**
     * 根据topId进行置顶
     *
     * @param topId topId
     */
    void updateTopToTopById(Long topId);

    /**
     * 批量根据topId删除置顶
     *
     * @param topIds topIds
     */
    void deleteTopsById(List<Long> topIds);

}
