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
import com.jinhx.blog.entity.operation.Top;
import com.jinhx.blog.mapper.operation.TopMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TopMapperService
 *
 * @author jinhx
 * @since 2019-02-22
 */
@Service
public class TopMapperService extends ServiceImpl<TopMapper, Top> {

    /**
     * 分页查询置顶列表
     *
     * @param page page
     * @param limit limit
     * @return 置顶列表
     */
    public IPage<Top> selectPage(Integer page, Integer limit) {
        return baseMapper.selectPage(new QueryPage<Top>(page, limit).getPage(),
                new LambdaQueryWrapper<Top>().orderByAsc(Top::getOrderNum));
    }

    /**
     * 根据topId查询置顶
     *
     * @param topId topId
     * @return 置顶
     */
    public Top selectTopById(Long topId) {
        List<Top> tops = selectTopsById(Lists.newArrayList(topId));
        if (CollectionUtils.isEmpty(tops)){
            return null;
        }

        return tops.get(0);
    }

    /**
     * 根据topId查询置顶列表
     *
     * @param topIds topIds
     * @return 置顶列表
     */
    public List<Top> selectTopsById(List<Long> topIds) {
        if (CollectionUtils.isEmpty(topIds)){
            return Lists.newArrayList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<Top>().in(Top::getTopId, topIds));
    }

    /**
     * 根据orderNum查询置顶数量
     *
     * @param orderNum orderNum
     * @return 置顶数量
     */
    public int selectTopCountByOrderNum(Integer orderNum) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Top>()
                .eq(Top::getOrderNum, orderNum));
    }

    /**
     * 根据orderNum查询置顶
     *
     * @param orderNum orderNum
     * @return 置顶
     */
    public Top selectTopByOrderNum(Integer orderNum) {
        List<Top> tops = baseMapper.selectList(new LambdaQueryWrapper<Top>()
                .eq(Top::getOrderNum, orderNum)
                .last("limit 1"));

        if (CollectionUtils.isEmpty(tops)) {
            return null;
        }

        return tops.get(0);
    }

    /**
     * 根据linkId，模块查询置顶
     *
     * @param linkId linkId
     * @param module module
     * @return 置顶
     */
    public Top selectTopByLinkIdAndModule(Long linkId, Integer module) {
        List<Top> tops = baseMapper.selectList(new LambdaQueryWrapper<Top>()
                .eq(Top::getLinkId, linkId)
                .eq(Top::getModule, module)
                .last("limit 1"));

        if (CollectionUtils.isEmpty(tops)){
            return null;
        }

        return tops.get(0);
    }

    /**
     * 新增置顶
     *
     * @param top top
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertTop(Top top) {
        insertTops(Lists.newArrayList(top));
    }

    /**
     * 批量新增置顶
     *
     * @param tops tops
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertTops(List<Top> tops) {
        if (CollectionUtils.isNotEmpty(tops)){
            if (tops.stream().mapToInt(item -> baseMapper.insert(item)).sum() != tops.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

    /**
     * 根据linkId，模块更新置顶
     *
     * @param top top
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTopByLinkIdAndModule(Top top) {
        if (baseMapper.update(top, new LambdaUpdateWrapper<Top>()
                .eq(Top::getLinkId, top.getLinkId())
                .eq(Top::getModule, top.getModule())
                .set(Top::getOrderNum, top.getOrderNum())) < 1) {
            throw new MyException(ResponseEnums.UPDATE_FAILR);
        }
    }

    /**
     * 根据orderNum倒叙查询置顶列表
     *
     * @return 置顶列表
     */
    public List<Top> selectTopsOrderByOrderNumDesc() {
        return baseMapper.selectList(new LambdaQueryWrapper<Top>().orderByDesc(Top::getOrderNum));
    }

    /**
     * 根据topId更新置顶
     *
     * @param top top
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTopById(Top top) {
        updateTopsById(Lists.newArrayList(top));
    }

    /**
     * 批量根据topId更新置顶
     *
     * @param tops tops
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTopsById(List<Top> tops) {
        if (CollectionUtils.isNotEmpty(tops)){
            if (tops.stream().mapToInt(item -> baseMapper.updateById(item)).sum() != tops.size()){
                throw new MyException(ResponseEnums.UPDATE_FAILR);
            }
        }
    }

    /**
     * 批量根据topId删除置顶
     *
     * @param topIds topIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTopsById(List<Long> topIds) {
        if (CollectionUtils.isNotEmpty(topIds)){
            if (baseMapper.deleteBatchIds(topIds) != topIds.size()){
                throw new MyException(ResponseEnums.DELETE_FAIL);
            }
        }
    }

    /**
     * 根据模块，linkId查询置顶数量
     *
     * @param module module
     * @param linkId linkId
     * @return 置顶数量
     */
    public int selectTopCountByOrderNum(Integer module, Long linkId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Top>()
                .eq(Top::getModule, module)
                .eq(Top::getLinkId, linkId));
    }

    /********************** portal ********************************/

    /**
     * 根据模块查询置顶列表
     *
     * @param module module
     * @return 置顶列表
     */
    public List<Top> selectPortalTopsByModule(Integer module) {
        return baseMapper.selectList(new LambdaQueryWrapper<Top>()
                .eq(Top::getModule, module)
                .orderByAsc(Top::getOrderNum)
                // 只能调用一次,多次调用以最后一次为准 有sql注入的风险,请谨慎使用
                .last("limit 10"));
    }

}
