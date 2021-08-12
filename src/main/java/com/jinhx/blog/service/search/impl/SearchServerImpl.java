package com.jinhx.blog.service.search.impl;

import com.google.common.collect.Lists;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.operation.Top;
import com.jinhx.blog.entity.search.vo.SearchListVO;
import com.jinhx.blog.service.operation.TopMapperService;
import com.jinhx.blog.service.search.ArticleEsServer;
import com.jinhx.blog.service.search.SearchServer;
import com.jinhx.blog.service.search.VideoEsServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * SearchServerImpl
 *
 * @author jinhx
 * @since 2019-04-11
 */
@Slf4j
@Service
public class SearchServerImpl implements SearchServer {

    @Autowired
    private ArticleEsServer articleEsServer;

    @Autowired
    private VideoEsServer videoEsServer;

    @Autowired
    private TopMapperService topMapperService;

    /**
     * 搜索，包括文章，视频
     *
     * @param keyword 关键字
     * @return 搜索结果，包括文章，视频
     */
    @Cacheable(value = RedisKeyConstants.SEARCHS, key = "#keyword")
    @Override
    public SearchListVO search(String keyword) throws Exception {
        // 处理文章
        List<ArticleVO> articleVOList = articleEsServer.searchArticleList(keyword);
        List<Top> articleTops = topMapperService.listTops(ModuleTypeConstants.ARTICLE);
        ArticleVO[] articleVOTopArray = new ArticleVO[articleVOList.size()];
        Queue<ArticleVO> articleVONoTopQueue = new LinkedList<>();
        List<ArticleVO> articleVOResultList = new ArrayList<>();

        Set<Integer> articleVOTopSet = new HashSet<>();
        Set<Integer> articleVONoToSet = new HashSet<>();

        if (!CollectionUtils.isEmpty(articleTops)){
            articleVOList.forEach(articleDTOListItem -> {
                articleTops.forEach(topsItem -> {
                    if(topsItem.getLinkId().equals(articleDTOListItem.getId())){
                        if (!articleVOTopSet.contains(articleDTOListItem.getId()) && !articleVONoToSet.contains(articleDTOListItem.getId())) {
                            articleDTOListItem.setTop(true);
                            articleVOTopArray[topsItem.getOrderNum()-1] = articleDTOListItem;
                            articleVOTopSet.add(articleDTOListItem.getId());
                        }
                    }else {
                        if (!articleVOTopSet.contains(articleDTOListItem.getId()) && !articleVONoToSet.contains(articleDTOListItem.getId())) {
                            articleVONoTopQueue.add(articleDTOListItem);
                            articleVONoToSet.add(articleDTOListItem.getId());
                        }
                    }
                });
            });
            for (int i = 0; i < articleVOTopArray.length; i++) {
                if (articleVOTopArray[i] == null){
                    articleVOTopArray[i] = articleVONoTopQueue.poll();
                }
            }
            articleVOResultList.addAll(Lists.newArrayList(articleVOTopArray));
        }else {
            articleVOResultList.addAll(articleVOList);
        }

        // 处理视频
//        List<VideoVO> videoVOList = videoEsServer.searchVideoList(keyword);
//        List<TopVO> videoTopVOs = topMapperService.listTopVO(ModuleTypeConstants.VIDEO);
//        VideoVO[] videoVOTopArray = new VideoVO[videoVOList.size()];
//        Queue<VideoVO> videoVONoTopQueue = new LinkedList<>();
//        List<VideoVO> videoVOResultList = new ArrayList<>();
//
//        Set<Integer> videoVOTopSet = new HashSet<>();
//        Set<Integer> videoVONoToSet = new HashSet<>();
//
//        if (!CollectionUtils.isEmpty(videoTopVOs)){
//            videoVOList.forEach(videoVOListItem -> {
//                videoTopVOs.forEach(topVOsItem -> {
//                    if(topVOsItem.getLinkId().equals(videoVOListItem.getId())){
//                        if (!videoVOTopSet.contains(videoVOListItem.getId()) && !videoVONoToSet.contains(videoVOListItem.getId())) {
//                            videoVOListItem.setTop(true);
//                            videoVOTopArray[topVOsItem.getOrderNum() - 1] = videoVOListItem;
//                            videoVOTopSet.add(videoVOListItem.getId());
//                        }
//                    }else {
//                        if (!videoVOTopSet.contains(videoVOListItem.getId()) && !videoVONoToSet.contains(videoVOListItem.getId())) {
//                            videoVONoTopQueue.add(videoVOListItem);
//                            videoVONoToSet.add(videoVOListItem.getId());
//                        }
//                    }
//                });
//            });
//            for (int i = 0; i < videoVOTopArray.length; i++) {
//                if (videoVOTopArray[i] == null){
//                    videoVOTopArray[i] = videoVONoTopQueue.poll();
//                }
//            }
//            videoVOResultList.addAll(Lists.newArrayList(videoVOTopArray));
//        }else {
//            videoVOResultList.addAll(videoVOList);
//        }

        SearchListVO searchListVO = new SearchListVO();
//        searchListVO.setVideoList(videoVOResultList);
        searchListVO.setArticleList(articleVOResultList);
        return searchListVO;
    }
}
