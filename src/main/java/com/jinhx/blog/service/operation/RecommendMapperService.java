package com.jinhx.blog.service.operation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.operation.Recommend;
import com.jinhx.blog.entity.operation.vo.HomeRecommendInfoVO;
import com.jinhx.blog.mapper.operation.RecommendMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RecommendMapperService
 *
 * @author jinhx
 * @since 2019-02-22
 */
@Service
public class RecommendMapperService extends ServiceImpl<RecommendMapper, Recommend> {

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    public HomeRecommendInfoVO selectHomeRecommendInfoVO() {
        HomeRecommendInfoVO homeRecommendInfoVO = new HomeRecommendInfoVO();
        homeRecommendInfoVO.setCount(baseMapper.selectCount(new LambdaQueryWrapper<>()));
        return homeRecommendInfoVO;
    }

    /**
     * 分页查询推荐列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @return 推荐列表
     */
    public IPage<Recommend> selectPage(Integer page, Integer limit) {
        return baseMapper.selectPage(new QueryPage<Recommend>(page, limit).getPage(),
                new LambdaQueryWrapper<Recommend>().orderByAsc(Recommend::getOrderNum));
    }

    /**
     * 根据recommendId查询推荐
     *
     * @param recommendId recommendId
     * @return 推荐
     */
    public Recommend selectRecommendById(Long recommendId) {
        List<Recommend> recommends = selectRecommendsById(Lists.newArrayList(recommendId));
        if (CollectionUtils.isEmpty(recommends)){
            return null;
        }

        return recommends.get(0);
    }

    /**
     * 根据recommendId查询推荐列表
     *
     * @param recommendIds recommendIds
     * @return 推荐列表
     */
    public List<Recommend> selectRecommendsById(List<Long> recommendIds) {
        if (CollectionUtils.isEmpty(recommendIds)){
            return Lists.newArrayList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<Recommend>().in(Recommend::getRecommendId, recommendIds));
    }

    /**
     * 根据orderNum查询推荐数量
     *
     * @param orderNum orderNum
     * @return 推荐数量
     */
    public int selectRecommendCountByOrderNum(Integer orderNum) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Recommend>()
                .eq(Recommend::getOrderNum, orderNum));
    }

    /**
     * 根据orderNum查询推荐
     *
     * @param orderNum orderNum
     * @return 推荐
     */
    public Recommend selectRecommendByOrderNum(Integer orderNum) {
        List<Recommend> recommends = baseMapper.selectList(new LambdaQueryWrapper<Recommend>()
                .eq(Recommend::getOrderNum, orderNum)
                .last("limit 1"));

        if (CollectionUtils.isEmpty(recommends)) {
            return null;
        }

        return recommends.get(0);
    }

    /**
     * 根据linkId，模块查询推荐
     *
     * @param linkId linkId
     * @param module module
     * @return 推荐
     */
    public Recommend selectRecommendByLinkIdAndModule(Long linkId, Integer module) {
        List<Recommend> recommends = baseMapper.selectList(new LambdaQueryWrapper<Recommend>()
                .eq(Recommend::getLinkId, linkId)
                .eq(Recommend::getModule, module)
                .last("limit 1"));

        if (CollectionUtils.isEmpty(recommends)){
            return null;
        }

        return recommends.get(0);
    }

    /**
     * 根据linkId，模块更新推荐
     *
     * @param recommend recommend
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRecommendByLinkIdAndModule(Recommend recommend) {
        if (baseMapper.update(recommend, new LambdaUpdateWrapper<Recommend>()
                .eq(Recommend::getLinkId, recommend.getLinkId())
                .eq(Recommend::getModule, recommend.getModule())
                .set(Recommend::getOrderNum, recommend.getOrderNum())) < 1) {
            throw new MyException(ResponseEnums.UPDATE_FAILR);
        }
    }

    /**
     * 新增推荐
     *
     * @param recommend recommend
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertRecommend(Recommend recommend) {
        insertRecommends(Lists.newArrayList(recommend));
    }

    /**
     * 批量新增推荐
     *
     * @param recommends recommends
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertRecommends(List<Recommend> recommends) {
        if (CollectionUtils.isNotEmpty(recommends)){
            if (recommends.stream().mapToInt(item -> baseMapper.insert(item)).sum() != recommends.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

    /**
     * 根据recommendId更新推荐
     *
     * @param recommend recommend
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRecommendById(Recommend recommend) {
        updateRecommendsById(Lists.newArrayList(recommend));
    }

    /**
     * 批量根据recommendId更新推荐
     *
     * @param recommends recommends
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRecommendsById(List<Recommend> recommends) {
        if (CollectionUtils.isNotEmpty(recommends)){
            if (recommends.stream().mapToInt(item -> baseMapper.updateById(item)).sum() != recommends.size()){
                throw new MyException(ResponseEnums.UPDATE_FAILR);
            }
        }
    }

    /**
     * 根据orderNum倒叙查询推荐列表
     *
     * @return 推荐列表
     */
    public List<Recommend> selectRecommendsOrderByOrderNumDesc() {
        return baseMapper.selectList(new LambdaQueryWrapper<Recommend>().orderByDesc(Recommend::getOrderNum));
    }

    /**
     * 批量根据recommendId删除推荐
     *
     * @param recommendIds recommendIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRecommendsById(List<Long> recommendIds) {
        if (CollectionUtils.isNotEmpty(recommendIds)){
            if (baseMapper.deleteBatchIds(recommendIds) != recommendIds.size()){
                throw new MyException(ResponseEnums.DELETE_FAIL);
            }
        }
    }

    /**
     * 批量根据linkId，模块删除推荐
     *
     * @param linkIds linkIds
     * @param module module
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRecommendsByLinkIdAndModule(List<Long> linkIds, Integer module) {
        baseMapper.delete(new LambdaQueryWrapper<Recommend>()
                .in(Recommend::getLinkId, linkIds)
                .eq(Recommend::getModule, module));
    }

    /**
     * 通过模块查询链接id列表
     *
     * @param module module
     * @return 链接id列表
     */
    public List<Long> selectLinkIdsByModule(Integer module) {
        List<Recommend> recommends = baseMapper.selectList(new LambdaQueryWrapper<Recommend>()
                .eq(Recommend::getModule, module)
                .select(Recommend::getLinkId));
        if (CollectionUtils.isEmpty(recommends)){
            return Collections.emptyList();
        }

        return recommends.stream().map(Recommend::getLinkId).distinct().collect(Collectors.toList());
    }

    /**
     * 查找最大顺序
     *
     * @return Integer
     */
    public int selectRecommendMaxOrderNum() {
        List<Recommend> recommends = baseMapper.selectList(new LambdaQueryWrapper<Recommend>()
                .select(Recommend::getOrderNum)
                .orderByDesc(Recommend::getOrderNum)
                .last("limit 1"));

        if (CollectionUtils.isEmpty(recommends)){
            return 1;
        }

        return recommends.get(0).getOrderNum();
    }

    /********************** portal ********************************/

    /**
     * 根据模块查询推荐列表
     *
     * @param module 模块
     * @return 推荐列表
     */
    public List<Recommend> selectPortalRecommendsByModule(Integer module) {
        return baseMapper.selectList(new LambdaQueryWrapper<Recommend>()
                .eq(Recommend::getModule, module)
                .orderByAsc(Recommend::getOrderNum));
    }

}
