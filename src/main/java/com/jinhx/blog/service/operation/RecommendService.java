package com.jinhx.blog.service.operation;

import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.operation.Recommend;
import com.jinhx.blog.entity.operation.vo.HomeRecommendInfoVO;
import com.jinhx.blog.entity.operation.vo.RecommendVO;

import java.util.List;

/**
 * RecommendService
 *
 * @author jinhx
 * @since 2019-02-22
 */
public interface RecommendService {

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    HomeRecommendInfoVO selectHomeRecommendInfoVO();

    /**
     * 分页查询推荐列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @return 推荐列表
     */
    PageData<RecommendVO> selectPage(Integer page, Integer limit);

    /**
     * 根据模块，标题查询推荐列表
     *
     * @param module module
     * @param title title
     * @return 推荐列表
     */
    List<RecommendVO> selectRecommendVOsByModuleAndTitle(Integer module, String title);

    /**
     * 根据recommendId查询推荐
     *
     * @param recommendId recommendId
     * @return 推荐
     */
    RecommendVO selectRecommendVOById(Long recommendId);

    /**
     * 新增推荐
     *
     * @param recommend recommend
     */
    void insertRecommend(Recommend recommend);

    /**
     * 根据linkId，模块更新推荐
     *
     * @param recommend recommend
     */
    void updateRecommendByLinkIdAndModule(Recommend recommend);

    /**
     * 根据recommendId更新推荐置顶
     *
     * @param recommendId recommendId
     */
    void updateRecommendToTopById(Long recommendId);

    /**
     * 批量根据friendLinkId删除推荐
     *
     * @param recommendIds recommendIds
     */
    void deleteRecommendsById(List<Long> recommendIds);

    /********************** portal ********************************/

    /**
     * 根据模块查询推荐列表
     *
     * @param module 模块
     * @return 推荐列表
     */
    List<RecommendVO> selectPortalRecommendVOsByModule(Integer module);

}
