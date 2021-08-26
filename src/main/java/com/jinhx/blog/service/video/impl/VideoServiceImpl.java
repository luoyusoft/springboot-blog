package com.jinhx.blog.service.video.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.constants.GitalkConstants;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.constants.RabbitMQConstants;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.filter.params.ParamsHttpServletRequestWrapper;
import com.jinhx.blog.common.threadpool.ThreadPoolEnum;
import com.jinhx.blog.common.util.*;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.gitalk.InitGitalkRequest;
import com.jinhx.blog.entity.operation.Recommend;
import com.jinhx.blog.entity.operation.TagLink;
import com.jinhx.blog.entity.operation.VideoAdaptorBuilder;
import com.jinhx.blog.entity.video.Video;
import com.jinhx.blog.entity.video.vo.HomeVideoInfoVO;
import com.jinhx.blog.entity.video.vo.VideoVO;
import com.jinhx.blog.mapper.video.VideoMapper;
import com.jinhx.blog.service.cache.CacheServer;
import com.jinhx.blog.service.operation.*;
import com.jinhx.blog.service.sys.SysUserMapperService;
import com.jinhx.blog.service.video.VideoMapperService;
import com.jinhx.blog.service.video.VideoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * VideoServiceImpl
 *
 * @author jinhx
 * @since 2018-11-22
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    // 每天重新计算点赞，key
    private static final String BLOG_VIDEO_LIKE_LOCK_KEY = "blog:video:like:lock:";

    @Autowired
    private TagMapperService tagMapperService;

    @Autowired
    private TagLinkMapperService tagLinkMapperService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RecommendMapperService recommendMapperService;

    @Autowired
    private TopMapperService topMapperService;

    @Autowired
    private CacheServer cacheServer;

    @Autowired
    private VideoMapperService videoMapperService;

    @Resource
    private RabbitMQUtils rabbitmqUtils;

    @Autowired
    private SysUserMapperService sysUserMapperService;

    /**
     * 将Video按需转换为VideoVO
     *
     * @param videoAdaptorBuilder videoAdaptorBuilder
     * @return VideoVO
     */
    @Override
    public VideoVO adaptorVideoToVideoVO(VideoAdaptorBuilder<Video> videoAdaptorBuilder){
        if(Objects.isNull(videoAdaptorBuilder) || Objects.isNull(videoAdaptorBuilder.getData())){
            return null;
        }

        Video video = videoAdaptorBuilder.getData();
        VideoVO videoVO = new VideoVO();
        BeanUtils.copyProperties(video, videoVO);

        if (videoAdaptorBuilder.getCategoryListStr()){
            videoVO.setCategoryListStr(categoryService.adaptorcategoryIdsToCategoryNames(videoVO.getCategoryId(), ModuleTypeConstants.VIDEO));
        }

        if (videoAdaptorBuilder.getTagList()){
            List<TagLink> tagLinks = tagLinkMapperService.selectTagLinksByLinkIdAndModule(videoVO.getVideoId(), ModuleTypeConstants.VIDEO);
            if (CollectionUtils.isNotEmpty(tagLinks)){
                videoVO.setTagList(tagMapperService.listByLinkId(tagLinks));
            }
        }

        if (videoAdaptorBuilder.getRecommend()){
            videoVO.setRecommend(Objects.nonNull(recommendMapperService.selectRecommendByLinkIdAndModule(videoVO.getVideoId(), ModuleTypeConstants.VIDEO)));
        }

        if (videoAdaptorBuilder.getTop()){
            videoVO.setTop(topMapperService.selectTopCountByOrderNum(ModuleTypeConstants.VIDEO, videoVO.getVideoId()) > 0);
        }

        if (videoAdaptorBuilder.getAuthor()){
            videoVO.setAuthor(sysUserMapperService.selectNicknameBySysUserId(videoVO.getCreaterId()));
        }

        return videoVO;
    }

    /**
     * 将VideoVO转换为Video
     *
     * @param videoAdaptorBuilder videoAdaptorBuilder
     * @return Video
     */
    @Override
    public Video adaptorVideoVOToVideo(VideoAdaptorBuilder<VideoVO> videoAdaptorBuilder){
        if(Objects.isNull(videoAdaptorBuilder) || Objects.isNull(videoAdaptorBuilder.getData())){
            return null;
        }

        Video video = new Video();
        BeanUtils.copyProperties(videoAdaptorBuilder.getData(), video);
        return video;
    }

    /**
     * 将Video列表按需转换为VideoVO列表
     *
     * @param videoAdaptorBuilder videoAdaptorBuilder
     * @return VideoVO列表
     */
    @Override
    public List<VideoVO> adaptorVideosToVideoVOs(VideoAdaptorBuilder<List<Video>> videoAdaptorBuilder){
        if(Objects.isNull(videoAdaptorBuilder) || CollectionUtils.isEmpty(videoAdaptorBuilder.getData())){
            return Collections.emptyList();
        }
        List<VideoVO> videoVOs = Lists.newArrayList();
        videoAdaptorBuilder.getData().forEach(video -> {
            if (Objects.isNull(video)){
                return;
            }

            videoVOs.add(adaptorVideoToVideoVO(new VideoAdaptorBuilder.Builder<Video>()
                    .setCategoryListStr(videoAdaptorBuilder.getCategoryListStr())
                    .setTagList(videoAdaptorBuilder.getTagList())
                    .setRecommend(videoAdaptorBuilder.getRecommend())
                    .setTop(videoAdaptorBuilder.getTop())
                    .setAuthor(videoAdaptorBuilder.getAuthor())
                    .build(video)));
        });

        return videoVOs;
    }

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    @Override
    public HomeVideoInfoVO getHommeVideoInfoVO() {
        return videoMapperService.getHommeVideoInfoVO();
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
    public PageData queryPage(Integer page, Integer limit, String title) {
        IPage<Video> videoIPage = videoMapperService.queryPage(page, limit, title);

        if (CollectionUtils.isEmpty(videoIPage.getRecords())){
            return new PageData(videoIPage);
        }

        List<VideoVO> videoVOs = adaptorVideosToVideoVOs(new VideoAdaptorBuilder.Builder<List<Video>>()
                .setCategoryListStr()
                .setTagList()
                .setRecommend()
                .setAuthor()
                .build(videoIPage.getRecords()));
        IPage<VideoVO> videoVOIPage = new Page<>();
        BeanUtils.copyProperties(videoIPage, videoVOIPage);
        videoVOIPage.setRecords(videoVOs);
        return new PageData(videoVOIPage);
    }

    /**
     * 保存视频
     *
     * @param videoVO videoVO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveVideo(VideoVO videoVO) {
        videoMapperService.saveVideo(videoVO);

        videoVO.getTagList().forEach(item -> {
            tagMapperService.saveTagAndNew(item);
            TagLink tagLink = new TagLink();
            tagLink.setLinkId(videoVO.getVideoId());
            tagLink.setTagId(item.getTagId());
            tagLink.setModule(ModuleTypeConstants.VIDEO);
            tagLinkMapperService.save(tagLink);
        });

        InitGitalkRequest initGitalkRequest = new InitGitalkRequest();
        initGitalkRequest.setId(videoVO.getVideoId());
        initGitalkRequest.setTitle(videoVO.getTitle());
        initGitalkRequest.setType(GitalkConstants.GITALK_TYPE_VIDEO);
        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_GITALK_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_GITALK_INIT_ROUTINGKEY, JsonUtils.objectToJson(initGitalkRequest));
        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_VIDEO_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_VIDEO_ADD_ROUTINGKEY,
                JsonUtils.objectToJson(videoMapperService.getVideo(videoVO.getVideoId(), Video.PUBLISH_TRUE)));

        cleanVideosCache(Lists.newArrayList());
    }

    /**
     * 更新视频
     *
     * @param videoVO videoVO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVideo(VideoVO videoVO) {
        Video video = videoMapperService.getVideo(videoVO.getVideoId(), null);
        if (Objects.isNull(video)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "视频不存在");
        }
        // 删除多对多所属标签
        tagLinkMapperService.deleteTagLinksByLinkIdAndModule(videoVO.getVideoId(), ModuleTypeConstants.VIDEO);

        // 更新所属标签
        videoVO.getTagList().forEach(item -> {
            tagMapperService.saveTagAndNew(item);
            TagLink tagLink = new TagLink();
            tagLink.setLinkId(videoVO.getVideoId());
            tagLink.setTagId(item.getTagId());
            tagLink.setModule(ModuleTypeConstants.VIDEO);
            tagLinkMapperService.save(tagLink);
        });

        // 更新视频
        videoMapperService.updateVideo(adaptorVideoVOToVideo(new VideoAdaptorBuilder.Builder<VideoVO>().build(videoVO)));

        if (videoVO.getRecommend() != null){
            if (videoVO.getRecommend()){
                if (Objects.isNull(recommendMapperService.selectRecommendByLinkIdAndModule(videoVO.getVideoId(), ModuleTypeConstants.VIDEO))){
                    Integer maxOrderNum = recommendMapperService.selectRecommendMaxOrderNum();
                    Recommend recommend = new Recommend();
                    recommend.setModule(ModuleTypeConstants.VIDEO);
                    recommend.setLinkId(videoVO.getVideoId());
                    recommend.setOrderNum(maxOrderNum + 1);
                    recommendMapperService.insertRecommend(recommend);
                }
            }else {
                if (Objects.nonNull(recommendMapperService.selectRecommendByLinkIdAndModule(videoVO.getVideoId(), ModuleTypeConstants.VIDEO))){
                    recommendMapperService.deleteRecommendsByLinkIdAndModule(Lists.newArrayList(videoVO.getVideoId()), ModuleTypeConstants.VIDEO);
                }
            }
        }

        // 发送rabbitmq消息同步到es
        InitGitalkRequest initGitalkRequest = new InitGitalkRequest();
        initGitalkRequest.setId(videoVO.getVideoId());
        initGitalkRequest.setTitle(videoVO.getTitle());
        initGitalkRequest.setType(GitalkConstants.GITALK_TYPE_VIDEO);
        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_GITALK_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_GITALK_INIT_ROUTINGKEY, JsonUtils.objectToJson(initGitalkRequest));
        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_VIDEO_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_VIDEO_UPDATE_ROUTINGKEY,
                JsonUtils.objectToJson(videoMapperService.getVideo(videoVO.getVideoId(), Video.PUBLISH_TRUE)));

        cleanVideosCache(Lists.newArrayList(videoVO.getVideoId()));
    }

    /**
     * 更新视频状态
     *
     * @param videoVO videoVO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVideoStatus(VideoVO videoVO) {
        Video video = videoMapperService.getVideo(videoVO.getVideoId(), null);
        if (Objects.isNull(video)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "视频不存在");
        }

        if (Objects.nonNull(videoVO.getPublish())){
            // 更新发布状态
            videoMapperService.updateVideoById(adaptorVideoVOToVideo(new VideoAdaptorBuilder.Builder<VideoVO>().build(videoVO)));

            if (videoVO.getPublish()){
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_VIDEO_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_VIDEO_ADD_ROUTINGKEY,
                        JsonUtils.objectToJson(videoMapperService.getVideo(videoVO.getVideoId(), Video.PUBLISH_TRUE)));
            }else {
                List<Long> videoIds = Lists.newArrayList(videoVO.getVideoId());
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_VIDEO_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_VIDEO_DELETE_ROUTINGKEY, JsonUtils.objectToJson(videoIds));
            }
        }

        if (Objects.nonNull(videoVO.getRecommend())){
            // 更新推荐状态
            if (videoVO.getRecommend()){
                if (Objects.isNull(recommendMapperService.selectRecommendByLinkIdAndModule(videoVO.getVideoId(), ModuleTypeConstants.VIDEO))){
                    Integer maxOrderNum = recommendMapperService.selectRecommendMaxOrderNum();
                    Recommend recommend = new Recommend();
                    recommend.setModule(ModuleTypeConstants.VIDEO);
                    recommend.setLinkId(videoVO.getVideoId());
                    recommend.setOrderNum(maxOrderNum + 1);
                    recommendMapperService.insertRecommend(recommend);
                }
            }else {
                if (Objects.nonNull(recommendMapperService.selectRecommendByLinkIdAndModule(videoVO.getVideoId(), ModuleTypeConstants.VIDEO))){
                    recommendMapperService.deleteRecommendsByLinkIdAndModule(Lists.newArrayList(videoVO.getVideoId()), ModuleTypeConstants.VIDEO);
                }
            }
        }

        cleanVideosCache(Lists.newArrayList(videoVO.getVideoId()));
    }

    /**
     * 获取视频对象
     *
     * @param videoId videoId
     * @param publish publish
     * @param videoAdaptorBuilder videoAdaptorBuilder
     * @return VideoVO
     */
    @Override
    public VideoVO getVideoVO(Long videoId, Boolean publish, VideoAdaptorBuilder<Video> videoAdaptorBuilder) {
        Video video = videoMapperService.getVideo(videoId, publish);
        if (Objects.isNull(video)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "视频不存在");
        }

        return adaptorVideoToVideoVO(videoAdaptorBuilder.setData(video));
    }

    /**
     * 判断类别下是否有视频
     *
     * @param categoryId categoryId
     * @return 类别下是否有视频
     */
    @Override
    public Boolean checkByCategoryId(Long categoryId) {
        return videoMapperService.checkByCategoryId(categoryId);
    }

    /**
     * 判断上传文件下是否有视频
     *
     * @param url url
     * @return 上传文件下是否有视频
     */
    @Override
    public Boolean checkByFile(String url) {
        return videoMapperService.checkByFile(url);
    }

    /**
     * 批量删除
     *
     * @param videoIds 视频id列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVideos(List<Long> videoIds) {
        videoIds.forEach(videoId -> {
            Video video = videoMapperService.getVideo(videoId, null);
            if (Objects.isNull(video)){
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "视频不存在");
            }

            //先删除视频标签多对多关联
            tagLinkMapperService.deleteTagLinksByLinkIdAndModule(videoId, ModuleTypeConstants.VIDEO);

            videoMapperService.deleteVideos(Lists.newArrayList(videoId));

            recommendMapperService.deleteRecommendsByLinkIdAndModule(Lists.newArrayList(videoId), ModuleTypeConstants.VIDEO);
        });

        // 发送rabbitmq消息同步到es
        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_VIDEO_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_VIDEO_DELETE_ROUTINGKEY, JsonUtils.objectToJson(videoIds));

        cleanVideosCache(videoIds);
    }

    /**
     * 查询所有已发布的视频
     *
     * @return 所有已发布的视频
     */
    @Override
    public List<Video> listVideosByPublish() {
        return videoMapperService.listVideosByPublish();
    }

    /**
     * 根据标题查询所有已发布的视频
     *
     * @param title 标题
     * @return 所有已发布的视频
     */
    @Override
    public List<Video> listVideosByPublishAndTitle(String title) {
        return videoMapperService.listVideosByPublishAndTitle(title);
    }

    /**
     * 清除缓存
     *
     * @param videoIds videoIds
     */
    private void cleanVideosCache(List<Long> videoIds){
        ThreadPoolEnum.COMMON.getThreadPoolExecutor().execute(() ->{
            cacheServer.cleanVideosCache(videoIds);
        });
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
    @Cacheable(value = RedisKeyConstants.VIDEOS)
    @Override
    public PageData listVideos(Integer page, Integer limit, Boolean latest, Long categoryId, Boolean like, Boolean watch) {
        IPage<Video> videoIPage = videoMapperService.listVideos(page, limit, latest, categoryId, like, watch);

        if (CollectionUtils.isEmpty(videoIPage.getRecords())){
            return new PageData(videoIPage);
        }

        List<VideoVO> videoVOs = adaptorVideosToVideoVOs(new VideoAdaptorBuilder.Builder<List<Video>>()
                .setTagList()
                .setAuthor()
                .build(videoIPage.getRecords()));
        IPage<VideoVO> videoVOIPage = new Page<>();
        BeanUtils.copyProperties(videoIPage, videoVOIPage);
        videoVOIPage.setRecords(videoVOs);
        return new PageData(videoVOIPage);
    }

    /**
     * 获取VideoVO
     *
     * @param id id
     * @return VideoVO
     */
    @Cacheable(value = RedisKeyConstants.VIDEO, key = "#id")
    @Override
    public VideoVO getVideoVO(Long id) {
        Video video = videoMapperService.getVideo(id, Video.PUBLISH_TRUE);
        if (Objects.isNull(video)){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "视频不存在");
        }


        VideoVO videoVO = adaptorVideoToVideoVO(new VideoAdaptorBuilder.Builder<Video>()
                .setTagList()
                .setAuthor()
                .build(video));
        // 浏览数量
        baseMapper.update(null, new LambdaUpdateWrapper<Video>()
                .eq(Video::getVideoId, video.getVideoId())
                .setSql("watch_num = watch_num + 1"));

        return videoVO;
    }

    /**
     * 获取热观榜
     *
     * @return 热观视频列表
     */
    @Cacheable(value = RedisKeyConstants.VIDEOS, key = "'hotwatch'")
    @Override
    public List<VideoVO> listHotWatchVideos() {
        List<Video> videos = videoMapperService.listHotWatchVideos();

        return adaptorVideosToVideoVOs(new VideoAdaptorBuilder.Builder<List<Video>>()
                .build(videos));
    }

    /**
     * 视频点赞
     *
     * @param id id
     * @return 点赞结果
     */
    @Override
    public Boolean updateVideo(Long id) throws Exception {
        //获取request
        ParamsHttpServletRequestWrapper request = (ParamsHttpServletRequestWrapper) HttpContextUtils.getHttpServletRequest();
        String userId = EncodeUtils.encoderByMD5(IPUtils.getIpAddr(request) + UserAgentUtils.getBrowserName(request) +
                UserAgentUtils.getBrowserVersion(request) + UserAgentUtils.getDeviceManufacturer(request) +
                UserAgentUtils.getDeviceType(request) + UserAgentUtils.getOsVersion(request));
        // 每天重新计算点赞
        if (!RedisUtils.setIfAbsent(BLOG_VIDEO_LIKE_LOCK_KEY + userId + ":" + id, "1", DateUtils.getRemainMilliSecondsOneDay())){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "1天只能点赞1次，请明天再来点赞");
        }

        return baseMapper.update(null, new LambdaUpdateWrapper<Video>()
                .eq(Video::getVideoId, id)
                .setSql("like_num = like_num + 1")) > 0;
    }

}
