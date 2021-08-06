package com.jinhx.blog.service.operation;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinhx.blog.entity.builder.RecommendAdaptorBuilder;
import com.jinhx.blog.common.util.PageUtils;
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
public interface RecommendService extends IService<Recommend> {

    /**
     * 将Recommend转换为RecommendVO
     *
     * @param recommendAdaptorBuilder recommendAdaptorBuilder
     * @return RecommendVO
     */
    RecommendVO adaptorRecommendToRecommendVO(RecommendAdaptorBuilder<Recommend> recommendAdaptorBuilder);

    /**
     * 将Recommend列表按需转换为RecommendVO列表
     *
     * @param recommendAdaptorBuilder recommendAdaptorBuilder
     * @return RecommendVO列表
     */
    List<RecommendVO> adaptorRecommendsToRecommendVOs(RecommendAdaptorBuilder<List<Recommend>> recommendAdaptorBuilder);

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    HomeRecommendInfoVO getHomeRecommendInfoVO();

    /**
     * 分页查询
     *
     * @param page 页码
     * @param limit 每页数量
     * @return 推荐列表
     */
     PageUtils queryPage(Integer page, Integer limit);

    /**
     * 获取推荐列表
     *
     * @param module module
     * @param title title
     * @return 推荐列表
     */
    List<RecommendVO> select(Integer module, String title);

    /**
     * 批量删除
     *
     * @param linkIds linkIds
     * @param module module
     */
    void deleteRecommendsByLinkIdsAndType(List<Integer> linkIds, int module);

    /**
     * 新增
     *
     * @param recommend recommend
     */
    void insertRecommend(Recommend recommend);

    /**
     * 更新
     *
     * @param recommend recommend
     */
    void updateRecommend(Recommend recommend);

    /**
     * 推荐置顶
     *
     * @param id id
     */
    void updateRecommendTop(Integer id);

    /**
     * 删除
     *
     * @param ids ids
     */
    void deleteRecommendsByIds(List<Integer> ids);

    /**
     * 查找
     *
     * @param linkId linkId
     * @param module module
     */
    Recommend selectRecommendByLinkIdAndType(Integer linkId, Integer module);

    /**
     * 通过模块查询链接id列表
     *
     * @param module module
     * @return List<Integer>
     */
    List<Integer> selectLinkIdsByModule(Integer module);

    /**
     * 查找最大顺序
     *
     * @return Integer
     */
    Integer selectRecommendMaxOrderNum();

    /********************** portal ********************************/

    /**
     * 获取推荐列表
     *
     * @param module 模块
     * @return 推荐列表
     */
    List<RecommendVO> listRecommends(Integer module);

}
