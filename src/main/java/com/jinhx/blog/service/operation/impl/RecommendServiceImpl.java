package com.jinhx.blog.service.operation.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.threadpool.ThreadPoolEnum;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.ArticleBuilder;
import com.jinhx.blog.entity.article.dto.ArticleVOsQueryDTO;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.operation.Recommend;
import com.jinhx.blog.entity.operation.RecommendAdaptorBuilder;
import com.jinhx.blog.entity.operation.VideoAdaptorBuilder;
import com.jinhx.blog.entity.operation.vo.HomeRecommendInfoVO;
import com.jinhx.blog.entity.operation.vo.RecommendVO;
import com.jinhx.blog.entity.video.Video;
import com.jinhx.blog.entity.video.vo.VideoVO;
import com.jinhx.blog.mapper.operation.RecommendMapper;
import com.jinhx.blog.service.article.ArticleMapperService;
import com.jinhx.blog.service.article.ArticleService;
import com.jinhx.blog.service.cache.CacheServer;
import com.jinhx.blog.service.operation.RecommendMapperService;
import com.jinhx.blog.service.operation.RecommendService;
import com.jinhx.blog.service.video.VideoMapperService;
import com.jinhx.blog.service.video.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * RecommendServiceImpl
 *
 * @author jinhx
 * @since 2019-02-22
 */
@Service
@Slf4j
public class RecommendServiceImpl extends ServiceImpl<RecommendMapper, Recommend> implements RecommendService {

    @Autowired
    private RecommendMapperService recommendMapperService;

    @Resource
    private ArticleMapperService articleMapperService;

    @Resource
    private ArticleService articleService;

    @Resource
    private VideoMapperService videoMapperService;

    @Resource
    private VideoService videoService;

    @Autowired
    private CacheServer cacheServer;

    /**
     * 将Recommend转换为RecommendVO
     *
     * @param recommendAdaptorBuilder recommendAdaptorBuilder
     * @return RecommendVO
     */
    @Override
    public RecommendVO adaptorRecommendToRecommendVO(RecommendAdaptorBuilder<Recommend> recommendAdaptorBuilder){
        if(Objects.isNull(recommendAdaptorBuilder) || Objects.isNull(recommendAdaptorBuilder.getData())){
            return null;
        }

        Recommend recommend = recommendAdaptorBuilder.getData();
        RecommendVO recommendVO = new RecommendVO();
        BeanUtils.copyProperties(recommend, recommendVO);

        if(ModuleTypeConstants.ARTICLE.equals(recommendVO.getModule())){
            ArticleVOsQueryDTO articleVOsQueryDTO = new ArticleVOsQueryDTO();
            articleVOsQueryDTO.setLogStr("con=info");
            articleVOsQueryDTO.setPublish(Article.PUBLISH_TRUE);
            articleVOsQueryDTO.setArticleIds(Lists.newArrayList(recommendVO.getLinkId()));
            articleVOsQueryDTO.setArticleBuilder(ArticleBuilder.builder()
                    .categoryListStr(true)
                    .tagList(true)
                    .recommend(true)
                    .top(true)
                    .author(true)
                    .build());

            List<ArticleVO> articleVOs = articleService.getArticleVOs(articleVOsQueryDTO);

            if (CollectionUtils.isNotEmpty(articleVOs) && Objects.nonNull(articleVOs.get(0))){
                ArticleVO articleVO = articleVOs.get(0);
                if (recommendAdaptorBuilder.getDescription()){
                    recommendVO.setDescription(articleVO.getDescription());
                }

                if (recommendAdaptorBuilder.getReadNum()){
                    recommendVO.setReadNum(articleVO.getReadNum());
                }

                if (recommendAdaptorBuilder.getLikeNum()){
                    recommendVO.setLikeNum(articleVO.getLikeNum());
                }

                if (recommendAdaptorBuilder.getCover()){
                    recommendVO.setCover(articleVO.getCover());
                }

                if (recommendAdaptorBuilder.getTagList()){
                    recommendVO.setTagList(articleVO.getTagList());
                }

                if (recommendAdaptorBuilder.getTitle()){
                    recommendVO.setTitle(articleVO.getTitle());
                }
            }
        }

        if(ModuleTypeConstants.VIDEO.equals(recommendVO.getModule())){
            VideoVO videoVO = videoService.getVideoVO(recommendVO.getLinkId(), Video.PUBLISH_TRUE, new VideoAdaptorBuilder.Builder<Video>().setAll().build());
            if (Objects.nonNull(videoVO)){
                if (recommendAdaptorBuilder.getWatchNum()){
                    recommendVO.setWatchNum(videoVO.getWatchNum());
                }

                if (recommendAdaptorBuilder.getLikeNum()){
                    recommendVO.setLikeNum(videoVO.getLikeNum());
                }

                if (recommendAdaptorBuilder.getCover()){
                    recommendVO.setCover(videoVO.getCover());
                }

                if (recommendAdaptorBuilder.getTagList()){
                    recommendVO.setTagList(videoVO.getTagList());
                }

                if (recommendAdaptorBuilder.getTitle()){
                    recommendVO.setTitle(videoVO.getTitle());
                }
            }
        }

        return recommendVO;
    }

    /**
     * 将Recommend列表按需转换为RecommendVO列表
     *
     * @param recommendAdaptorBuilder recommendAdaptorBuilder
     * @return RecommendVO列表
     */
    @Override
    public List<RecommendVO> adaptorRecommendsToRecommendVOs(RecommendAdaptorBuilder<List<Recommend>> recommendAdaptorBuilder){
        if(Objects.isNull(recommendAdaptorBuilder) || CollectionUtils.isEmpty(recommendAdaptorBuilder.getData())){
            return Collections.emptyList();
        }
        List<RecommendVO> recommendVOs = Lists.newArrayList();
        recommendAdaptorBuilder.getData().forEach(recommend -> {
            if (Objects.isNull(recommend)){
                return;
            }

            recommendVOs.add(adaptorRecommendToRecommendVO(new RecommendAdaptorBuilder.Builder<Recommend>()
                    .setDescription(recommendAdaptorBuilder.getDescription())
                    .setReadNum(recommendAdaptorBuilder.getReadNum())
                    .setWatchNum(recommendAdaptorBuilder.getWatchNum())
                    .setLikeNum(recommendAdaptorBuilder.getLikeNum())
                    .setCover(recommendAdaptorBuilder.getCover())
                    .setTagList(recommendAdaptorBuilder.getTagList())
                    .setTitle(recommendAdaptorBuilder.getTitle())
                    .build(recommend)));
        });

        return recommendVOs;
    }

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    @Override
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
    @Override
    public PageData queryPage(Integer page, Integer limit) {
        IPage<Recommend> recommendIPage = recommendMapperService.queryPage(page, limit);

        if (CollectionUtils.isEmpty(recommendIPage.getRecords())){
            return new PageData(recommendIPage);
        }

        List<RecommendVO> recommendVOs = adaptorRecommendsToRecommendVOs(new RecommendAdaptorBuilder.Builder<List<Recommend>>()
                .setTitle()
                .build(recommendIPage.getRecords()));

        IPage<RecommendVO> recommendVOIPage = new Page<>();
        BeanUtils.copyProperties(recommendIPage, recommendVOIPage);
        recommendVOIPage.setRecords(recommendVOs);

        return new PageData(recommendVOIPage);
    }

    /**
     * 获取推荐列表
     *
     * @param module module
     * @param title title
     * @return 推荐列表
     */
    @Override
    public List<RecommendVO> select(Integer module, String title) {
        List<RecommendVO> recommendVOList = new ArrayList<>();

        if (ModuleTypeConstants.ARTICLE.equals(module)){
            List<Article> articles = articleMapperService.listArticlesByPublishAndTitle(title);
            if (CollectionUtils.isNotEmpty(articles)){
                articles.forEach(articlesItem -> {
                    RecommendVO recommendVO = new RecommendVO();
                    recommendVO.setTitle(articlesItem.getTitle());
                    recommendVO.setLinkId(articlesItem.getId());
                    recommendVO.setModule(module);
                    recommendVOList.add(recommendVO);
                });
            }
        }

        if (ModuleTypeConstants.VIDEO.equals(module)){
            List<Video> videoList = videoMapperService.listVideosByPublishAndTitle(title);
            if (CollectionUtils.isNotEmpty(videoList)){
                videoList.forEach(videoListItem -> {
                    RecommendVO recommendVO = new RecommendVO();
                    recommendVO.setTitle(videoListItem.getTitle());
                    recommendVO.setLinkId(videoListItem.getId());
                    recommendVO.setModule(module);
                    recommendVOList.add(recommendVO);
                });
            }
        }

        return recommendVOList;
    }

    /**
     * 批量删除
     *
     * @param linkIds linkIds
     * @param module module
     */
    @Override
    public void deleteRecommendsByLinkIdsAndType(List<Integer> linkIds, int module) {
        baseMapper.delete(new LambdaQueryWrapper<Recommend>()
                .in(Recommend::getLinkId, linkIds)
                .eq(Recommend::getModule, module));

        cleanRecommendAllCache();
    }

    /**
     * 新增
     *
     * @param recommend recommend
     */
    @Override
    public void insertRecommend(Recommend recommend) {
        if (baseMapper.selectCount(new LambdaQueryWrapper<Recommend>()
                .eq(Recommend::getOrderNum, recommend.getOrderNum())) > 0){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该顺序已被占用");
        }
        if (ModuleTypeConstants.ARTICLE.equals(recommend.getModule())){
            Article article = articleMapperService.getArticle(recommend.getLinkId(), Article.PUBLISH_TRUE);
            if(Objects.isNull(article)) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "推荐内容不存在");
            }
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
            Video video = videoMapperService.getVideo(recommend.getLinkId(), Video.PUBLISH_TRUE);
            if(Objects.isNull(video)) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "推荐内容不存在");
            }
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
        cleanRecommendAllCache();
    }

    /**
     * 更新
     *
     * @param recommend recommend
     */
    @Override
    public void updateRecommend(Recommend recommend) {
        Recommend existRecommend = baseMapper.selectOne(new LambdaQueryWrapper<Recommend>()
                .eq(Recommend::getOrderNum, recommend.getOrderNum()));
        if (Objects.nonNull(existRecommend) && !existRecommend.getId().equals(recommend.getId())){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该顺序已被占用");
        }
        if (ModuleTypeConstants.ARTICLE.equals(recommend.getModule())){
            Article article = articleMapperService.getArticle(recommend.getLinkId(), Article.PUBLISH_TRUE);
            if(Objects.isNull(article)) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "推荐内容不存在");
            }
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
            Video video = videoMapperService.getVideo(recommend.getLinkId(), Video.PUBLISH_TRUE);
            if(Objects.isNull(video)) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "推荐内容不存在");
            }
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
        cleanRecommendAllCache();
    }

    /**
     * 推荐置顶
     *
     * @param id id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
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

        cleanRecommendAllCache();
    }

    /**
     * 删除
     *
     * @param ids ids
     */
    @Override
    public void deleteRecommendsByIds(List<Integer> ids) {
        baseMapper.deleteBatchIds(ids);
        cleanRecommendAllCache();
    }

    /**
     * 查找
     *
     * @param linkId linkId
     * @param module module
     */
    @Override
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
    @Override
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
    @Override
    public Integer selectRecommendMaxOrderNum() {
        return baseMapper.selectList(new LambdaQueryWrapper<Recommend>()
                .select(Recommend::getOrderNum)
                .orderByDesc(Recommend::getOrderNum)
                .last("limit 1")).get(0).getOrderNum();
    }

    /**
     * 清除缓存
     */
    private void cleanRecommendAllCache(){
        ThreadPoolEnum.COMMON.getThreadPoolExecutor().execute(() ->{
            cacheServer.cleanRecommendAllCache();
        });
    }

    /********************** portal ********************************/

    /**
     * 获取推荐列表
     *
     * @param module 模块
     * @return 推荐列表
     */
    @Cacheable(value = RedisKeyConstants.RECOMMENDS, key = "#module")
    @Override
    public List<RecommendVO> listRecommends(Integer module) {
        List<Recommend> recommends = recommendMapperService.listRecommends(module);
        if (CollectionUtils.isEmpty(recommends)){
            return Collections.emptyList();
        }

        return adaptorRecommendsToRecommendVOs(new RecommendAdaptorBuilder.Builder<List<Recommend>>()
                .setAll()
                .build(recommends));
    }

}
