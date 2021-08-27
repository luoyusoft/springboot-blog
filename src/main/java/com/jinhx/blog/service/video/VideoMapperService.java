package com.jinhx.blog.service.video;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.video.Video;
import com.jinhx.blog.entity.video.vo.HomeVideoInfoVO;
import com.jinhx.blog.mapper.video.VideoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * VideoMapperService
 *
 * @author jinhx
 * @since 2018-11-22
 */
@Service
public class VideoMapperService extends ServiceImpl<VideoMapper, Video> {

    /**
     * 查询首页信息
     *
     * @return 首页信息
     */
    public HomeVideoInfoVO selectHommeVideoInfoVO() {
        Integer publishCount = baseMapper.selectCount(new LambdaQueryWrapper<Video>()
                .eq(Video::getPublish, Video.PUBLISH_TRUE));
        Integer allCount = baseMapper.selectCount(new LambdaQueryWrapper<>());

        HomeVideoInfoVO homeVideoInfoVO = new HomeVideoInfoVO();
        homeVideoInfoVO.setPublishCount(publishCount);
        homeVideoInfoVO.setAllCount(allCount);
        return homeVideoInfoVO;
    }

    /**
     * 分页查询视频列表
     *
     * @param page 页码
     * @param limit 页数
     * @param title 标题
     * @return 视频列表
     */
    public IPage<Video> selectPage(Integer page, Integer limit, String title) {
        return baseMapper.selectPage(new QueryPage<Video>(page, limit).getPage(), new LambdaQueryWrapper<Video>()
                .like(ObjectUtil.isNotEmpty(title), Video::getTitle, title)
                .like(ObjectUtil.isNotEmpty(title), Video::getAlternateName, title)
                .orderByDesc(Video::getUpdateTime));
    }

    /**
     * 新增视频
     *
     * @param video 视频
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertVideo(Video video) {
        insertVideos(Lists.newArrayList(video));
    }

    /**
     * 批量新增视频
     *
     * @param videos 视频列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertVideos(List<Video> videos) {
        if (CollectionUtils.isNotEmpty(videos)){
            if (videos.stream().mapToInt(item -> baseMapper.insert(item)).sum() != videos.size()){
                throw new MyException(ResponseEnums.INSERT_FAIL);
            }
        }
    }

    /**
     * 根据videoId更新视频
     *
     * @param video video
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateVideoById(Video video) {
        updateVideosById(Lists.newArrayList(video));
    }

    /**
     * 批量根据videoId更新视频
     *
     * @param videos videos
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateVideosById(List<Video> videos) {
        if (CollectionUtils.isNotEmpty(videos)){
            if (videos.stream().mapToInt(item -> baseMapper.updateById(item)).sum() != videos.size()){
                throw new MyException(ResponseEnums.UPDATE_FAILR);
            }
        }
    }

    /**
     * 根据视频id，发布状态查询视频
     *
     * @param videoId videoId
     * @param publish publish
     * @return 视频
     */
    public Video selectVideoByIdAndPublish(Long videoId, Boolean publish) {
        List<Video> videos = selectVideosByIdAndPublish(Lists.newArrayList(videoId), publish);
        if (CollectionUtils.isEmpty(videos)){
            return null;
        }

        return videos.get(0);
    }

    /**
     * 根据视频id列表，发布状态查询视频列表
     *
     * @param videoIds videoIds
     * @param publish publish
     * @return 视频列表
     */
    public List<Video> selectVideosByIdAndPublish(List<Long> videoIds, Boolean publish) {
        if (CollectionUtils.isEmpty(videoIds)){
            return Lists.newArrayList();
        }

        return baseMapper.selectList(new LambdaQueryWrapper<Video>()
                .in(CollectionUtils.isNotEmpty(videoIds), Video::getVideoId, videoIds)
                .eq(Objects.nonNull(publish), Video::getPublish, publish));
    }

    /**
     * 查询类别下是否有视频
     *
     * @param categoryId categoryId
     * @return 类别下是否有视频
     */
    public Boolean existByCategoryId(Long categoryId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Video>()
                .like(Objects.nonNull(categoryId), Video::getCategoryId, categoryId)) > 0;
    }

    /**
     * 查询上传文件是否有视频封面或视频内容占用
     *
     * @param url url
     * @return 上传文件是否有视频封面或视频内容占用
     */
    public Boolean existByFile(String url) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Video>()
                .eq(ObjectUtil.isNotEmpty(url), Video::getCover, url)
                .or()
                .eq(ObjectUtil.isNotEmpty(url), Video::getVideoUrl, url)) > 0;
    }

    /**
     * 批量根据videoId删除视频
     *
     * @param videoIds videoIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteVideosById(List<Long> videoIds) {
        if (CollectionUtils.isNotEmpty(videoIds)){
            if (baseMapper.deleteBatchIds(videoIds) != videoIds.size()){
                throw new MyException(ResponseEnums.DELETE_FAIL);
            }
        }
    }

    /**
     * 根据发布状态查询视频列表
     *
     * @param publish publish
     * @return 视频列表
     */
    public List<Video> selectVideosByPublish(Boolean publish) {
        return baseMapper.selectList(new LambdaQueryWrapper<Video>()
                .eq(Objects.nonNull(publish), Video::getPublish, publish));
    }

    /**
     * 根据标题，发布状态查询视频列表
     *
     * @param title 标题
     * @param publish publish
     * @return 视频列表
     */
    public List<Video> selectVideosByPublishAndTitle(String title, Boolean publish) {
        return baseMapper.selectList(new LambdaQueryWrapper<Video>()
                .eq(Video::getPublish, publish)
                .and(qw ->
                        qw.like(ObjectUtil.isNotEmpty(title), Video::getTitle, title)
                                .or()
                                .like(ObjectUtil.isNotEmpty(title), Video::getAlternateName, title))
                .orderByDesc(Video::getVideoId));
    }

    /********************** portal ********************************/

    /**
     * 分页查询视频列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @param categoryId 分类
     * @param latest 时间排序
     * @param like 点赞量排序
     * @param watch 观看量排序
     * @return 视频列表
     */
    public IPage<Video> selectPortalPage(Integer page, Integer limit, Boolean latest, Long categoryId, Boolean like, Boolean watch) {
        return baseMapper.selectPage(new QueryPage<Video>(page, limit).getPage(), new LambdaQueryWrapper<Video>()
                .eq(Video::getPublish, Video.PUBLISH_TRUE)
                .like(categoryId != null, Video::getCategoryId, categoryId)
                .orderByDesc(latest, Video::getCreateTime)
                .orderByDesc(like, Video::getLikeNum)
                .orderByDesc(watch, Video::getWatchNum));
    }

    /**
     * 查询热观视频列表
     *
     * @return 热观视频列表
     */
    public List<Video> selectHotReadVideoVOs() {
        return baseMapper.selectList(new LambdaQueryWrapper<Video>()
                .eq(Video::getPublish, Video.PUBLISH_TRUE)
                // 只能调用一次,多次调用以最后一次为准 有sql注入的风险,请谨慎使用
                .last("limit 5")
                .orderByDesc(Video::getWatchNum));
    }

    /**
     * 视频观看
     *
     * @param videoId videoId
     */
    @Transactional(rollbackFor = Exception.class)
    public void addVideoWatchNum(Long videoId) {
        if (baseMapper.update(null, new LambdaUpdateWrapper<Video>()
                .eq(Video::getVideoId, videoId)
                .setSql("watch_num = watch_num + 1")) != 1) {
            throw new MyException(ResponseEnums.UPDATE_FAILR);
        }
    }

    /**
     * 视频点赞
     *
     * @param videoId videoId
     */
    @Transactional(rollbackFor = Exception.class)
    public void addVideoLikeNum(Long videoId) {
        if (baseMapper.update(null, new LambdaUpdateWrapper<Video>()
                .eq(Video::getVideoId, videoId)
                .setSql("like_num = like_num + 1")) != 1) {
            throw new MyException(ResponseEnums.UPDATE_FAILR);
        }
    }

}
