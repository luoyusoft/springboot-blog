package com.jinhx.blog.mapper.operation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinhx.blog.entity.operation.Recommend;
import com.jinhx.blog.entity.operation.vo.RecommendVO;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * RecommendMapper
 *
 * @author jinhx
 * @since 2019-02-22
 */
public interface RecommendMapper extends BaseMapper<Recommend> {

    /**
     * 获取总量
     * @return 总量
     */
    Integer selectCount();

    /**
     * 获取
     * @return
     */
    Recommend selectRecommendByLinkIdAndType(Integer linkId, Integer module);

    /**
     * 获取
     * @return
     */
    Recommend selectRecommendByOrderNum(Integer orderNum);

    /**
     * 获取列表
     * @return
     */
    List<Recommend> selectRecommends();

    /**
     * 批量修改顺序，注意从大的开始，不然会有唯一索引冲突
     * @return
     */
    Integer updateRecommendsOrderNumById(List<Recommend> recommends);

    /**
     * 更新
     * @return
     */
    Boolean updateRecommendOrderNumByLinkIdAndType(Recommend recommend);

    /**
     * 推荐置顶
     * @param id
     */
    Boolean updateRecommendOrderNumById(@Param("orderNum") Integer orderNum, @Param("id") Integer id);

    /**
     * 查找最大顺序
     */
    Integer selectRecommendMaxOrderNum();

    /********************** portal ********************************/

    /**
     * 获取推荐列表
     * @return
     */
    List<RecommendVO> listRecommendDTO(Integer module);

}
