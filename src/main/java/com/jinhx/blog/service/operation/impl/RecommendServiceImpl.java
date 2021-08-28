package com.jinhx.blog.service.operation.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.jinhx.blog.service.article.ArticleMapperService;
import com.jinhx.blog.service.article.ArticleService;
import com.jinhx.blog.service.cache.CacheServer;
import com.jinhx.blog.service.operation.RecommendMapperService;
import com.jinhx.blog.service.operation.RecommendService;
import com.jinhx.blog.service.video.VideoMapperService;
import com.jinhx.blog.service.video.VideoService;
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

/**
 * RecommendServiceImpl
 *
 * @author jinhx
 * @since 2019-02-22
 */
@Service
public class RecommendServiceImpl implements RecommendService {

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
    private RecommendVO adaptorRecommendToRecommendVO(RecommendAdaptorBuilder<Recommend> recommendAdaptorBuilder){
        if(Objects.isNull(recommendAdaptorBuilder) || Objects.isNull(recommendAdaptorBuilder.getData())){
            return null;
        }

        Recommend recommend = recommendAdaptorBuilder.getData();
        RecommendVO recommendVO = new RecommendVO();
        BeanUtils.copyProperties(recommend, recommendVO);

        if(ModuleTypeConstants.ARTICLE.equals(recommendVO.getModule())){
            ArticleVOsQueryDTO articleVOsQueryDTO = new ArticleVOsQueryDTO();
            articleVOsQueryDTO.setLogStr("con=info");
            articleVOsQueryDTO.setPublish(Article.PublishEnum.YES.getCode());
            articleVOsQueryDTO.setArticleIds(Lists.newArrayList(recommendVO.getLinkId()));
            articleVOsQueryDTO.setArticleBuilder(ArticleBuilder.builder()
                    .categoryListStr(true)
                    .tagList(true)
                    .recommend(true)
                    .top(true)
                    .author(true)
                    .build());

            List<ArticleVO> articleVOs = articleService.selectArticleVOs(articleVOsQueryDTO);

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
            VideoVO videoVO = videoService.selectVideoVOByIdAndPublish(recommendVO.getLinkId(), Video.PublishEnum.YES.getCode(), new VideoAdaptorBuilder.Builder<Video>().setAll().build());
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
    private List<RecommendVO> adaptorRecommendsToRecommendVOs(RecommendAdaptorBuilder<List<Recommend>> recommendAdaptorBuilder){
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
    public HomeRecommendInfoVO selectHomeRecommendInfoVO() {
        return recommendMapperService.selectHomeRecommendInfoVO();
    }

    /**
     * 分页查询推荐列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @return 推荐列表
     */
    @Override
    public PageData<RecommendVO> selectPage(Integer page, Integer limit) {
        IPage<Recommend> recommendIPage = recommendMapperService.selectPage(page, limit);

        if (CollectionUtils.isEmpty(recommendIPage.getRecords())){
            return new PageData<>();
        }

        List<RecommendVO> recommendVOs = adaptorRecommendsToRecommendVOs(new RecommendAdaptorBuilder.Builder<List<Recommend>>()
                .setTitle()
                .build(recommendIPage.getRecords()));

        IPage<RecommendVO> recommendVOIPage = new Page<>();
        BeanUtils.copyProperties(recommendIPage, recommendVOIPage);
        recommendVOIPage.setRecords(recommendVOs);

        return new PageData<>(recommendVOIPage);
    }

    /**
     * 根据模块，标题查询推荐列表
     *
     * @param module module
     * @param title title
     * @return 推荐列表
     */
    @Override
    public List<RecommendVO> selectRecommendVOsByModuleAndTitle(Integer module, String title) {
        List<RecommendVO> recommendVOList = new ArrayList<>();

        if (ModuleTypeConstants.ARTICLE.equals(module)){
            List<Article> articles = articleMapperService.selectArticlesByTitleAndPublish(title, Article.PublishEnum.YES.getCode());
            if (CollectionUtils.isNotEmpty(articles)){
                articles.forEach(articlesItem -> {
                    RecommendVO recommendVO = new RecommendVO();
                    recommendVO.setTitle(articlesItem.getTitle());
                    recommendVO.setLinkId(articlesItem.getArticleId());
                    recommendVO.setModule(module);
                    recommendVOList.add(recommendVO);
                });
            }
        }

        if (ModuleTypeConstants.VIDEO.equals(module)){
            List<Video> videoList = videoMapperService.selectVideosByPublishAndTitle(title, Video.PublishEnum.YES.getCode());
            if (CollectionUtils.isNotEmpty(videoList)){
                videoList.forEach(videoListItem -> {
                    RecommendVO recommendVO = new RecommendVO();
                    recommendVO.setTitle(videoListItem.getTitle());
                    recommendVO.setLinkId(videoListItem.getVideoId());
                    recommendVO.setModule(module);
                    recommendVOList.add(recommendVO);
                });
            }
        }

        return recommendVOList;
    }

    /**
     * 根据recommendId查询推荐
     *
     * @param recommendId recommendId
     * @return 推荐
     */
    @Override
    public RecommendVO selectRecommendVOById(Long recommendId) {
        Recommend recommend = recommendMapperService.selectRecommendById(recommendId);

        if (Objects.isNull(recommend)){
            return null;
        }

        return adaptorRecommendToRecommendVO(new RecommendAdaptorBuilder.Builder<Recommend>()
                .setTitle()
                .build(recommend));
    }

    /**
     * 新增推荐
     *
     * @param recommend recommend
     */
    @Override
    public void insertRecommend(Recommend recommend) {
        if (recommendMapperService.selectRecommendCountByOrderNum(recommend.getOrderNum()) > 0){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该顺序已被占用");
        }

        verifyExistByLinkIdAndModule(recommend.getLinkId(), recommend.getModule());

        Recommend oldRecommend = recommendMapperService.selectRecommendByLinkIdAndModule(recommend.getLinkId(), recommend.getModule());
        if(Objects.isNull(oldRecommend)){
            recommendMapperService.insertRecommend(recommend);
        }else {
            recommendMapperService.updateRecommendByLinkIdAndModule(recommend);
        }

        cleanRecommendAllCache();
    }

    /**
     * 根据linkId，模块校验是否存在推荐内容
     *
     * @param linkId linkId
     * @param module module
     */
    private void verifyExistByLinkIdAndModule(Long linkId, Integer module) {
        if (ModuleTypeConstants.ARTICLE.equals(module)){
            if(Objects.isNull(articleMapperService.selectArticleByIdAndPublish(linkId, Article.PublishEnum.YES.getCode()))) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "推荐内容不存在");
            }
            return;
        }

        if (ModuleTypeConstants.VIDEO.equals(module)){
            if(Objects.isNull(videoMapperService.selectVideoByIdAndPublish(linkId, Video.PublishEnum.YES.getCode()))) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "推荐内容不存在");
            }
            return;
        }

        throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "推荐模块不存在");
    }

    /**
     * 根据linkId，模块更新推荐
     *
     * @param recommend recommend
     */
    @Override
    public void updateRecommendByLinkIdAndModule(Recommend recommend) {
        Recommend existRecommend = recommendMapperService.selectRecommendByOrderNum(recommend.getOrderNum());
        if (Objects.nonNull(existRecommend) && !existRecommend.getRecommendId().equals(recommend.getRecommendId())){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该顺序已被占用");
        }

        verifyExistByLinkIdAndModule(recommend.getLinkId(), recommend.getModule());

        Recommend oldRecommend = recommendMapperService.selectRecommendByLinkIdAndModule(recommend.getLinkId(), recommend.getModule());
        if(Objects.isNull(oldRecommend)){
            recommendMapperService.insertRecommend(recommend);
        }else {
            recommendMapperService.updateRecommendByLinkIdAndModule(recommend);
        }

        cleanRecommendAllCache();
    }

    /**
     * 根据recommendId更新推荐置顶
     *
     * @param recommendId recommendId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateRecommendToTopById(Long recommendId) {
        // 现将所有记录顺序加1，然后再置顶
        if (recommendMapperService.selectRecommendCountByOrderNum(Recommend.ORDER_NUM_TOP) > 0) {
            List<Recommend> recommends = recommendMapperService.selectRecommendsOrderByOrderNumDesc();

            recommends.forEach(recommendsItem -> {
                // 修改顺序，注意从大的开始，不然会有唯一索引冲突
                recommendsItem.setOrderNum(recommendsItem.getOrderNum() + 1);
            });

            recommendMapperService.updateRecommendsById(recommends);
        }

        Recommend recommend = new Recommend();
        recommend.setRecommendId(recommendId);
        recommend.setOrderNum(Recommend.ORDER_NUM_TOP);
        recommendMapperService.updateRecommendById(recommend);

        cleanRecommendAllCache();
    }

    /**
     * 批量根据friendLinkId删除推荐
     *
     * @param recommendIds recommendIds
     */
    @Override
    public void deleteRecommendsById(List<Long> recommendIds) {
        recommendMapperService.deleteRecommendsById(recommendIds);
        cleanRecommendAllCache();
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
     * 根据模块查询推荐列表
     *
     * @param module 模块
     * @return 推荐列表
     */
    @Cacheable(value = RedisKeyConstants.RECOMMENDS, key = "#module")
    @Override
    public List<RecommendVO> selectPortalRecommendVOsByModule(Integer module) {
        List<Recommend> recommends = recommendMapperService.selectPortalRecommendsByModule(module);
        if (CollectionUtils.isEmpty(recommends)){
            return Collections.emptyList();
        }

        return adaptorRecommendsToRecommendVOs(new RecommendAdaptorBuilder.Builder<List<Recommend>>()
                .setAll()
                .build(recommends));
    }

}
