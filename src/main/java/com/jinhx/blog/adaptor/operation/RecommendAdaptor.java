package com.jinhx.blog.adaptor.operation;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.operation.Recommend;
import com.jinhx.blog.entity.operation.vo.RecommendVO;
import com.jinhx.blog.entity.video.Video;
import com.jinhx.blog.entity.video.vo.VideoVO;
import com.jinhx.blog.service.article.ArticleService;
import com.jinhx.blog.service.video.VideoService;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * RecommendAdaptor
 *
 * @author jinhx
 * @since 2021-07-21
 */
@Component
public class RecommendAdaptor {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private VideoService videoService;

    /**
     * 将Recommend转换为RecommendVO
     *
     * @param recommendAdaptorBuilder recommendAdaptorBuilder
     * @return RecommendVO
     */
    public RecommendVO adaptorRecommendToRecommendVO(RecommendAdaptorBuilder<Recommend> recommendAdaptorBuilder){
        if(ObjectUtils.isNull(recommendAdaptorBuilder) || ObjectUtils.isNull(recommendAdaptorBuilder.getData())){
            return null;
        }

        Recommend recommend = recommendAdaptorBuilder.getData();
        RecommendVO recommendVO = new RecommendVO();
        BeanUtils.copyProperties(recommend, recommendVO);

        if(ModuleTypeConstants.ARTICLE.equals(recommendVO.getModule())){
            ArticleVO articleVO = articleService.getArticleVO(recommendVO.getLinkId(), Article.PUBLISH_TRUE);
            if (ObjectUtils.isNotNull(articleVO)){
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
            VideoVO videoVO = videoService.getVideoVO(recommendVO.getLinkId(), Video.PUBLISH_TRUE);
            if (ObjectUtils.isNotNull(videoVO)){
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
    public List<RecommendVO> adaptorRecommendsToRecommendVOs(RecommendAdaptorBuilder<List<Recommend>> recommendAdaptorBuilder){
        if(ObjectUtils.isNull(recommendAdaptorBuilder) || CollectionUtils.isEmpty(recommendAdaptorBuilder.getData())){
            return Collections.emptyList();
        }
        List<RecommendVO> recommendVOs = Lists.newArrayList();
        recommendAdaptorBuilder.getData().forEach(recommend -> {
            if (ObjectUtils.isNull(recommend)){
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

}
