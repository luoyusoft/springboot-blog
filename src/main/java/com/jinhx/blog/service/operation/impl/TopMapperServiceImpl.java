package com.jinhx.blog.service.operation.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.adaptor.operation.TopAdaptor;
import com.jinhx.blog.adaptor.operation.TopAdaptorBuilder;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.PageUtils;
import com.jinhx.blog.common.util.Query;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.operation.Top;
import com.jinhx.blog.entity.operation.vo.TopVO;
import com.jinhx.blog.entity.video.Video;
import com.jinhx.blog.mapper.operation.TopMapper;
import com.jinhx.blog.service.article.ArticleMapperService;
import com.jinhx.blog.service.cache.CacheServer;
import com.jinhx.blog.service.operation.TopMapperService;
import com.jinhx.blog.service.video.VideoMapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
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
        return baseMapper.selectOne(new LambdaQueryWrapper<Top>()
                .orderByDesc(Top::getOrderNum)).getOrderNum();
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
