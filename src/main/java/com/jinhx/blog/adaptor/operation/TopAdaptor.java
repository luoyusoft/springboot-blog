package com.jinhx.blog.adaptor.operation;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.operation.Top;
import com.jinhx.blog.entity.operation.vo.TopVO;
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
 * TopAdaptor
 *
 * @author jinhx
 * @since 2021-07-21
 */
@Component
public class TopAdaptor {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private VideoService videoService;

    /**
     * 将Top转换为TopVO
     *
     * @param topAdaptorBuilder topAdaptorBuilder
     * @return TopVO
     */
    public TopVO adaptorTopToTopVO(TopAdaptorBuilder<Top> topAdaptorBuilder){
        if(ObjectUtils.isNull(topAdaptorBuilder) || ObjectUtils.isNull(topAdaptorBuilder.getData())){
            return null;
        }

        Top top = topAdaptorBuilder.getData();
        TopVO topVO = new TopVO();
        BeanUtils.copyProperties(top, topVO);

        if(ModuleTypeConstants.ARTICLE.equals(topVO.getModule())){
            ArticleVO articleVO = articleService.getArticleVO(topVO.getLinkId(), Article.PUBLISH_TRUE);
            if (ObjectUtils.isNotNull(articleVO)){
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
            VideoVO videoVO = videoService.getVideoVO(topVO.getLinkId(), Video.PUBLISH_TRUE);
            if (ObjectUtils.isNotNull(videoVO)){
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
    public List<TopVO> adaptorTopsToTopVOs(TopAdaptorBuilder<List<Top>> topAdaptorBuilder){
        if(ObjectUtils.isNull(topAdaptorBuilder) || CollectionUtils.isEmpty(topAdaptorBuilder.getData())){
            return Collections.emptyList();
        }
        List<TopVO> topVOs = Lists.newArrayList();
        topAdaptorBuilder.getData().forEach(top -> {
            if (ObjectUtils.isNull(top)){
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

}
