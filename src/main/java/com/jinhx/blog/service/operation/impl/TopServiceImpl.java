package com.jinhx.blog.service.operation.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.threadpool.ThreadPoolEnum;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.ArticleBuilder;
import com.jinhx.blog.entity.article.dto.ArticleVOsQueryDTO;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.operation.*;
import com.jinhx.blog.entity.operation.vo.TopVO;
import com.jinhx.blog.entity.video.Video;
import com.jinhx.blog.entity.video.vo.VideoVO;
import com.jinhx.blog.service.article.ArticleMapperService;
import com.jinhx.blog.service.article.ArticleService;
import com.jinhx.blog.service.cache.CacheServer;
import com.jinhx.blog.service.operation.TopMapperService;
import com.jinhx.blog.service.operation.TopService;
import com.jinhx.blog.service.video.VideoMapperService;
import com.jinhx.blog.service.video.VideoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * TopServiceImpl
 *
 * @author jinhx
 * @since 2019-02-22
 */
@Service
public class TopServiceImpl implements TopService {

    @Resource
    private ArticleMapperService articleMapperService;

    @Resource
    private VideoMapperService videoMapperService;

    @Resource
    private ArticleService articleService;

    @Resource
    private VideoService videoService;

    @Autowired
    private CacheServer cacheServer;

    @Autowired
    private TopMapperService topMapperService;

    /**
     * 将Top转换为TopVO
     *
     * @param topAdaptorBuilder topAdaptorBuilder
     * @return TopVO
     */
    private TopVO adaptorTopToTopVO(TopAdaptorBuilder<Top> topAdaptorBuilder){
        if(Objects.isNull(topAdaptorBuilder) || Objects.isNull(topAdaptorBuilder.getData())){
            return null;
        }

        Top top = topAdaptorBuilder.getData();
        TopVO topVO = new TopVO();
        BeanUtils.copyProperties(top, topVO);

        if(ModuleTypeConstants.ARTICLE.equals(topVO.getModule())){
            ArticleVOsQueryDTO articleVOsQueryDTO = new ArticleVOsQueryDTO();
            articleVOsQueryDTO.setLogStr("con=info");
            articleVOsQueryDTO.setPublish(Article.PUBLISH_TRUE);
            articleVOsQueryDTO.setArticleIds(Lists.newArrayList(topVO.getLinkId()));
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
                if (topAdaptorBuilder.getDescription()){
                    topVO.setDescription(articleVO.getDescription());
                }

                if (topAdaptorBuilder.getReadNum()){
                    topVO.setReadNum(articleVO.getReadNum());
                }

                if (topAdaptorBuilder.getLikeNum()){
                    topVO.setLikeNum(articleVO.getLikeNum());
                }

                if (topAdaptorBuilder.getCover()){
                    topVO.setCover(articleVO.getCover());
                }

                if (topAdaptorBuilder.getTagList()){
                    topVO.setTagList(articleVO.getTagList());
                }

                if (topAdaptorBuilder.getTitle()){
                    topVO.setTitle(articleVO.getTitle());
                }
            }
        }

        if(ModuleTypeConstants.VIDEO.equals(topVO.getModule())){
            VideoVO videoVO = videoService.selectVideoVOByIdAndPublish(topVO.getLinkId(), Video.PUBLISH_TRUE, new VideoAdaptorBuilder.Builder<Video>().setAll().build());
            if (Objects.nonNull(videoVO)){
                if (topAdaptorBuilder.getWatchNum()){
                    topVO.setWatchNum(videoVO.getWatchNum());
                }

                if (topAdaptorBuilder.getLikeNum()){
                    topVO.setLikeNum(videoVO.getLikeNum());
                }

                if (topAdaptorBuilder.getCover()){
                    topVO.setCover(videoVO.getCover());
                }

                if (topAdaptorBuilder.getTagList()){
                    topVO.setTagList(videoVO.getTagList());
                }

                if (topAdaptorBuilder.getTitle()){
                    topVO.setTitle(videoVO.getTitle());
                }
            }
        }

        return topVO;
    }

    /**
     * 将Top列表按需转换为TopVO列表
     *
     * @param topAdaptorBuilder topAdaptorBuilder
     * @return TopVO列表
     */
    private List<TopVO> adaptorTopsToTopVOs(TopAdaptorBuilder<List<Top>> topAdaptorBuilder){
        if(Objects.isNull(topAdaptorBuilder) || CollectionUtils.isEmpty(topAdaptorBuilder.getData())){
            return Collections.emptyList();
        }
        List<TopVO> topVOs = Lists.newArrayList();
        topAdaptorBuilder.getData().forEach(top -> {
            if (Objects.isNull(top)){
                return;
            }

            topVOs.add(adaptorTopToTopVO(new TopAdaptorBuilder.Builder<Top>()
                    .setDescription(topAdaptorBuilder.getDescription())
                    .setReadNum(topAdaptorBuilder.getReadNum())
                    .setWatchNum(topAdaptorBuilder.getWatchNum())
                    .setLikeNum(topAdaptorBuilder.getLikeNum())
                    .setCover(topAdaptorBuilder.getCover())
                    .setTagList(topAdaptorBuilder.getTagList())
                    .setTitle(topAdaptorBuilder.getTitle())
                    .build(top)));
        });

        return topVOs;
    }

    /**
     * 分页查询置顶列表
     *
     * @param page page
     * @param limit limit
     * @return 置顶列表
     */
    @Override
    public PageData<TopVO> selectPage(Integer page, Integer limit) {
        IPage<Top> topIPage = topMapperService.selectPage(page, limit);

        if (CollectionUtils.isEmpty(topIPage.getRecords())){
            return new PageData<>();
        }

        List<TopVO> topVOs = adaptorTopsToTopVOs(new TopAdaptorBuilder.Builder<List<Top>>()
                .setTitle()
                .build(topIPage.getRecords()));

        IPage<TopVO> topVOIPage = new Page<>();
        BeanUtils.copyProperties(topIPage, topVOIPage);
        topVOIPage.setRecords(topVOs);

        return new PageData<>(topVOIPage);
    }

    /**
     * 根据模块，标题查询置顶列表
     *
     * @param module module
     * @param title title
     * @return 置顶列表
     */
    @Override
    public List<TopVO> selectTopVOsByModuleAndTitle(Integer module, String title) {
        List<TopVO> TopVOList = Lists.newArrayList();

        if (ModuleTypeConstants.ARTICLE.equals(module)){
            List<Article> articles = articleMapperService.selectArticlesByTitleAndPublish(title, Article.PUBLISH_TRUE);
            if (CollectionUtils.isNotEmpty(articles)){
                articles.forEach(articlesItem -> {
                    TopVO TopVO = new TopVO();
                    TopVO.setTitle(articlesItem.getTitle());
                    TopVO.setLinkId(articlesItem.getArticleId());
                    TopVO.setModule(module);
                    TopVOList.add(TopVO);
                });
            }
        }

        if (ModuleTypeConstants.VIDEO.equals(module)){
            List<Video> videoList = videoMapperService.selectVideosByPublishAndTitle(title, Video.PUBLISH_TRUE);
            if (videoList != null && videoList.size() > 0){
                videoList.forEach(videoListItem -> {
                    TopVO TopVO = new TopVO();
                    TopVO.setTitle(videoListItem.getTitle());
                    TopVO.setLinkId(videoListItem.getVideoId());
                    TopVO.setModule(module);
                    TopVOList.add(TopVO);
                });
            }
        }

        return TopVOList;
    }

    /**
     * 根据topId查询置顶
     *
     * @param topId topId
     * @return 置顶
     */
    @Override
    public TopVO selectTopVOById(Long topId) {
        Top top = topMapperService.selectTopById(topId);

        if (Objects.isNull(top)){
            return null;
        }

        return adaptorTopToTopVO(new TopAdaptorBuilder.Builder<Top>()
                .setTitle()
                .build(top));
    }

    /**
     * 新增置顶
     *
     * @param top top
     */
    @Override
    public void insertTop(Top top) {
        if (topMapperService.selectTopCountByOrderNum(top.getOrderNum()) > 0) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该顺序已被占用");
        }

        verifyExistByLinkIdAndModule(top.getLinkId(), top.getModule());

        Top oldTop = topMapperService.selectTopByLinkIdAndModule(top.getLinkId(), top.getModule());
        if(Objects.isNull(oldTop)){
            topMapperService.insertTop(top);
        }else {
            topMapperService.updateTopByLinkIdAndModule(top);
        }

        cleanListAllCache();
    }

    /**
     * 根据linkId，模块校验是否存在置顶内容
     *
     * @param linkId linkId
     * @param module module
     */
    private void verifyExistByLinkIdAndModule(Long linkId, Integer module) {
        if (ModuleTypeConstants.ARTICLE.equals(module)){
            if(Objects.isNull(articleMapperService.selectArticleByIdAndPublish(linkId, Article.PUBLISH_TRUE))) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "置顶内容不存在");
            }
            return;
        }

        if (ModuleTypeConstants.VIDEO.equals(module)){
            if(Objects.isNull(videoMapperService.selectVideoByIdAndPublish(linkId, Video.PUBLISH_TRUE))) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "置顶内容不存在");
            }
            return;
        }

        throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "置顶模块不存在");
    }

    /**
     * 根据topId更新置顶
     *
     * @param top top
     */
    @Override
    public void updateTopById(Top top) {
        Top existTop = topMapperService.selectTopByOrderNum(top.getOrderNum());
        if (Objects.nonNull(existTop) && !existTop.getTopId().equals(top.getTopId())){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该顺序已被占用");
        }

        verifyExistByLinkIdAndModule(top.getLinkId(), top.getModule());

        Top oldTop = topMapperService.selectTopByLinkIdAndModule(top.getLinkId(), top.getModule());
        if(Objects.isNull(oldTop)){
            topMapperService.insertTop(top);
        }else {
            topMapperService.updateTopByLinkIdAndModule(top);
        }

        cleanListAllCache();
    }

    /**
     * 根据topId进行置顶
     *
     * @param topId topId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateTopToTopById(Long topId) {
        // 现将所有记录顺序加1，然后再置顶
        if (topMapperService.selectTopCountByOrderNum(Top.ORDER_NUM_TOP) > 0) {
            List<Top> tops = topMapperService.selectTopsOrderByOrderNumDesc();

            tops.forEach(topsItem -> {
                // 修改顺序，注意从大的开始，不然会有唯一索引冲突
                topsItem.setOrderNum(topsItem.getOrderNum() + 1);
            });

            topMapperService.updateTopsById(tops);
        }

        Top top = new Top();
        top.setTopId(topId);
        top.setOrderNum(Top.ORDER_NUM_TOP);
        topMapperService.updateTopById(top);

        cleanListAllCache();
    }

    /**
     * 批量根据topId删除置顶
     *
     * @param topIds topIds
     */
    @Override
    public void deleteTopsById(List<Long> topIds) {
        topMapperService.deleteTopsById(topIds);
        cleanListAllCache();
    }

    /**
     * 清除缓存
     */
    private void cleanListAllCache(){
        ThreadPoolEnum.COMMON.getThreadPoolExecutor().execute(() ->{
            cacheServer.cleanListAllCache();
        });
    }

}
