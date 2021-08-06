package com.jinhx.blog.service.operation.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.operation.Top;
import com.jinhx.blog.mapper.operation.TopMapper;
import com.jinhx.blog.service.operation.TopMapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TopServiceImpl
 *
 * @author jinhx
 * @since 2019-02-22
 */
@Service
@Slf4j
public class TopMapperServiceImpl extends ServiceImpl<TopMapper, Top> implements TopMapperService {

    /**
     * 置顶
     *
     * @param id id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateTopTop(Integer id) {
        if (baseMapper.selectCount(new LambdaQueryWrapper<Top>()
                .eq(Top::getOrderNum, Top.ORDER_NUM_TOP)) > 0) {
            List<Top> Tops = baseMapper.selectList(new LambdaQueryWrapper<Top>()
                    .orderByDesc(Top::getOrderNum));
            Tops.forEach(topsItem -> {
                topsItem.setOrderNum(topsItem.getOrderNum() + 1);
                // 修改顺序，注意从大的开始，不然会有唯一索引冲突
                baseMapper.update(topsItem, new LambdaUpdateWrapper<Top>()
                        .eq(Top::getId, topsItem.getId())
                        .set(Top::getOrderNum, topsItem.getOrderNum()));
            });
        }

        if(baseMapper.update(null, new LambdaUpdateWrapper<Top>()
                .eq(Top::getId, id)
                .set(Top::getOrderNum, Top.ORDER_NUM_TOP)) < 1){
            throw new MyException(ResponseEnums.UPDATE_FAILR.getCode(), "更新数据失败");
        }
    }

    /**
     * 删除
     *
     * @param ids ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTopsByIds(List<Integer> ids) {
        baseMapper.deleteBatchIds(ids);
    }

    /**
     * 查找最大顺序
     *
     * @return 最大顺序
     */
    @Override
    public Integer selectTopMaxOrderNum() {
        return baseMapper.selectList(new LambdaQueryWrapper<Top>()
                .select(Top::getOrderNum)
                .orderByDesc(Top::getOrderNum)
                .last("limit 1")).get(0).getOrderNum();
    }

    /**
     * 是否已置顶
     *
     * @param module module
     * @param linkId linkId
     * @return 是否已置顶
     */
    @Override
    public Boolean isTopByModuleAndLinkId(Integer module, Integer linkId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Top>()
                .eq(Top::getModule, module)
                .eq(Top::getLinkId, linkId)) > 0;
    }

    /********************** portal ********************************/

    /**
     * 查询列表
     *
     * @param module module
     * @return List<Top>
     */
    @Override
    public List<Top> listTops(Integer module) {
        return baseMapper.selectList(new LambdaQueryWrapper<Top>()
                .eq(Top::getModule, module)
                .orderByAsc(Top::getOrderNum)
                // 只能调用一次,多次调用以最后一次为准 有sql注入的风险,请谨慎使用
                .last("limit 10"));
    }

}
