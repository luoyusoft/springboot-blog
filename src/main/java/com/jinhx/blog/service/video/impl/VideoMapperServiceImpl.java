package com.jinhx.blog.service.video.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.common.util.Query;
import com.jinhx.blog.entity.video.Video;
import com.jinhx.blog.entity.video.vo.HomeVideoInfoVO;
import com.jinhx.blog.mapper.video.VideoMapper;
import com.jinhx.blog.service.video.VideoMapperService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * VideoMapperServiceImpl
 *
 * @author jinhx
 * @since 2018-11-22
 */
@Service
public class VideoMapperServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoMapperService {

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    @Override
    public HomeVideoInfoVO getHommeVideoInfoVO() {
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
    @Override
    public IPage<Video> queryPage(Integer page, Integer limit, String title) {
        return baseMapper.selectPage(new Query<Video>(page, limit).getPage(), new LambdaQueryWrapper<Video>()
                .like(ObjectUtil.isNotEmpty(title), Video::getTitle, title)
                .like(ObjectUtil.isNotEmpty(title), Video::getAlternateName, title)
                .orderByDesc(Video::getUpdateTime));
    }

    /**
     * 保存视频
     *
     * @param video video
     */
    @Override
    public void saveVideo(Video video) {
        baseMapper.insert(video);
    }

    /**
     * 更新视频
     *
     * @param video video
     */
    @Override
    public void updateVideo(Video video) {
        baseMapper.updateById(video);
    }

    /**
     * 更新视频
     *
     * @param video video
     */
    @Override
    public void updateVideoById(Video video) {
        baseMapper.updateById(video);
    }

    /**
     * 获取视频对象
     *
     * @param videoId videoId
     * @param publish publish
     * @return Video
     */
    @Override
    public Video getVideo(Integer videoId, Boolean publish) {
        return baseMapper.selectOne(new LambdaQueryWrapper<Video>()
                .eq(Video::getId, videoId)
                .eq(publish != null, Video::getPublish, publish));
    }

    /**
     * 判断类别下是否有视频
     *
     * @param categoryId categoryId
     * @return 类别下是否有视频
     */
    @Override
    public Boolean checkByCategoryId(Integer categoryId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Video>()
                .like(categoryId != null, Video::getCategoryId, categoryId)) > 0;
    }

    /**
     * 判断上传文件下是否有视频
     *
     * @param url url
     * @return 上传文件下是否有视频
     */
    @Override
    public Boolean checkByFile(String url) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Video>()
                .eq(ObjectUtil.isNotEmpty(url), Video::getCover, url)
                .or()
                .eq(ObjectUtil.isNotEmpty(url), Video::getVideoUrl, url)) > 0;
    }

    /**
     * 批量删除
     *
     * @param ids 视频id数组
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVideos(List<Integer> ids) {
        baseMapper.deleteBatchIds(ids);
    }

    /**
     * 查询所有已发布的视频
     *
     * @return 所有已发布的视频
     */
    @Override
    public List<Video> listVideosByPublish() {
        return baseMapper.selectList(new LambdaQueryWrapper<Video>()
                .eq(Video::getPublish, Video.PUBLISH_TRUE)
                .orderByDesc(Video::getId));
    }

    /**
     * 根据标题查询所有已发布的视频
     *
     * @param title 标题
     * @return 所有已发布的视频
     */
    @Override
    public List<Video> listVideosByPublishAndTitle(String title) {
        return baseMapper.selectList(new LambdaQueryWrapper<Video>()
                .eq(Video::getPublish, Video.PUBLISH_TRUE)
                .and(qw ->
                        qw.like(ObjectUtil.isNotEmpty(title), Video::getTitle, title)
                                .or()
                                .like(ObjectUtil.isNotEmpty(title), Video::getAlternateName, title))
                .orderByDesc(Video::getId));
    }

    /********************** portal ********************************/

    /**
     * 分页获取视频列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @param categoryId 分类
     * @param latest 时间排序
     * @param like 点赞量排序
     * @param watch 观看量排序
     * @return 视频列表
     */
    @Override
    public IPage<Video> listVideos(Integer page, Integer limit, Boolean latest, Integer categoryId, Boolean like, Boolean watch) {
        return baseMapper.selectPage(new Query<Video>(page, limit).getPage(), new LambdaQueryWrapper<Video>()
                .eq(Video::getPublish, Video.PUBLISH_TRUE)
                .like(categoryId != null, Video::getCategoryId, categoryId)
                .orderByDesc(latest, Video::getCreateTime)
                .orderByDesc(like, Video::getLikeNum)
                .orderByDesc(watch, Video::getWatchNum));
    }

    /**
     * 获取热观榜
     *
     * @return 热观视频列表
     */
    @Override
    public List<Video> listHotWatchVideos() {
        return baseMapper.selectList(new LambdaQueryWrapper<Video>()
                .eq(Video::getPublish, Video.PUBLISH_TRUE)
                // 只能调用一次,多次调用以最后一次为准 有sql注入的风险,请谨慎使用
                .last("limit 5")
                .orderByDesc(Video::getWatchNum));
    }

}
