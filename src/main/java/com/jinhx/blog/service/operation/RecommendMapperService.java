package com.jinhx.blog.service.operation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.operation.Recommend;
import com.jinhx.blog.entity.operation.vo.HomeRecommendInfoVO;
import com.jinhx.blog.mapper.operation.RecommendMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * RecommendMapperService
 *
 * @author jinhx
 * @since 2019-02-22
 */
@Service
@Slf4j
public class RecommendMapperService extends ServiceImpl<RecommendMapper, Recommend> {

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    public HomeRecommendInfoVO getHomeRecommendInfoVO() {
        HomeRecommendInfoVO homeRecommendInfoVO = new HomeRecommendInfoVO();
        homeRecommendInfoVO.setCount(baseMapper.selectCount(new LambdaQueryWrapper<>()));
        return homeRecommendInfoVO;
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param limit 每页数量
     * @return 推荐列表
     */
    public IPage<Recommend> queryPage(Integer page, Integer limit) {
        return baseMapper.selectPage(new QueryPage<Recommend>(page, limit).getPage(),
                new LambdaQueryWrapper<Recommend>().orderByAsc(Recommend::getOrderNum));
    }

    /**
     * 批量删除
     *
     * @param linkIds linkIds
     * @param module module
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRecommendsByLinkIdsAndType(List<Integer> linkIds, int module) {
        baseMapper.delete(new LambdaQueryWrapper<Recommend>()
                .in(Recommend::getLinkId, linkIds)
                .eq(Recommend::getModule, module));
    }

    /**
     * 新增
     *
     * @param recommend recommend
     */
    public void insertRecommend(Recommend recommend) {
        if (baseMapper.selectCount(new LambdaQueryWrapper<Recommend>()
                .eq(Recommend::getOrderNum, recommend.getOrderNum())) > 0){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该顺序已被占用");
        }
        if (ModuleTypeConstants.ARTICLE.equals(recommend.getModule())){
//            Article article = articleMapperService.getArticle(recommend.getLinkId(), Article.PUBLISH_TRUE);
//            if(Objects.isNull(article)) {
//                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "推荐内容不存在");
//            }
            Recommend oldRecommend = baseMapper.selectOne(new LambdaQueryWrapper<Recommend>()
                    .eq(Recommend::getLinkId, recommend.getLinkId())
                    .eq(Recommend::getModule, recommend.getModule()));
            if(Objects.isNull(oldRecommend)){
                baseMapper.insert(recommend);
            }else {
                baseMapper.update(recommend, new LambdaUpdateWrapper<Recommend>()
                        .eq(Recommend::getLinkId, recommend.getLinkId())
                        .eq(Recommend::getModule, recommend.getModule())
                        .set(Recommend::getOrderNum, recommend.getOrderNum()));
            }
        }

        if (ModuleTypeConstants.VIDEO.equals(recommend.getModule())){
//            Video video = videoMapperService.getVideo(recommend.getLinkId(), Video.PUBLISH_TRUE);
//            if(Objects.isNull(video)) {
//                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "推荐内容不存在");
//            }
            Recommend oldRecommend = baseMapper.selectOne(new LambdaQueryWrapper<Recommend>()
                    .eq(Recommend::getLinkId, recommend.getLinkId())
                    .eq(Recommend::getModule, recommend.getModule()));
            if(Objects.isNull(oldRecommend)){
                baseMapper.insert(recommend);
            }else {
                baseMapper.update(recommend, new LambdaUpdateWrapper<Recommend>()
                        .eq(Recommend::getLinkId, recommend.getLinkId())
                        .eq(Recommend::getModule, recommend.getModule())
                        .set(Recommend::getOrderNum, recommend.getOrderNum()));
            }
        }
    }

    /**
     * 推荐置顶
     *
     * @param id id
     */
    public void updateRecommendTop(Integer id) {
        if (baseMapper.selectCount(new LambdaQueryWrapper<Recommend>()
                .eq(Recommend::getOrderNum, Recommend.ORDER_NUM_TOP)) > 0) {
            List<Recommend> recommends = baseMapper.selectList(new LambdaQueryWrapper<Recommend>()
                    .orderByDesc(Recommend::getOrderNum));
            recommends.forEach(recommendsItem -> {
                recommendsItem.setOrderNum(recommendsItem.getOrderNum() + 1);
                // 修改顺序，注意从大的开始，不然会有唯一索引冲突
                baseMapper.update(recommendsItem, new LambdaUpdateWrapper<Recommend>()
                        .eq(Recommend::getId, recommendsItem.getId())
                        .set(Recommend::getOrderNum, recommendsItem.getOrderNum()));
            });
        }

        if (baseMapper.update(null, new LambdaUpdateWrapper<Recommend>()
                .eq(Recommend::getId, id)
                .set(Recommend::getOrderNum, Recommend.ORDER_NUM_TOP)) < 1) {
            throw new MyException(ResponseEnums.UPDATE_FAILR.getCode(), "更新数据失败");
        }

    }

    /**
     * 删除
     *
     * @param ids ids
     */
    public void deleteRecommendsByIds(List<Integer> ids) {
        baseMapper.deleteBatchIds(ids);
    }

    /**
     * 查找
     *
     * @param linkId linkId
     * @param module module
     */
    public Recommend selectRecommendByLinkIdAndType(Integer linkId, Integer module) {
        return baseMapper.selectOne(new LambdaQueryWrapper<Recommend>()
                .eq(Recommend::getLinkId, linkId)
                .eq(Recommend::getModule, module));
    }

    /**
     * 通过模块查询链接id列表
     *
     * @param module module
     * @return List<Integer>
     */
    public List<Integer> selectLinkIdsByModule(Integer module) {
        List<Recommend> recommends = baseMapper.selectList(new LambdaQueryWrapper<Recommend>()
                .eq(Recommend::getModule, module));
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
    public Integer selectRecommendMaxOrderNum() {
        return baseMapper.selectList(new LambdaQueryWrapper<Recommend>()
                .select(Recommend::getOrderNum)
                .orderByDesc(Recommend::getOrderNum)
                .last("limit 1")).get(0).getOrderNum();
    }

    /********************** portal ********************************/

    /**
     * 获取推荐列表
     *
     * @param module 模块
     * @return 推荐列表
     */
    public List<Recommend> listRecommends(Integer module) {
        return  baseMapper.selectList(new LambdaQueryWrapper<Recommend>()
                .eq(Recommend::getModule, module)
                .orderByAsc(Recommend::getOrderNum));
    }

}
