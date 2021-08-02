package com.jinhx.blog.service.video.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinhx.blog.common.config.params.ParamsHttpServletRequestWrapper;
import com.jinhx.blog.common.constants.GitalkConstants;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.constants.RabbitMQConstants;
import com.jinhx.blog.common.constants.RedisKeyConstants;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.util.*;
import com.jinhx.blog.entity.gitalk.InitGitalkRequest;
import com.jinhx.blog.entity.operation.Category;
import com.jinhx.blog.entity.operation.Recommend;
import com.jinhx.blog.entity.operation.TagLink;
import com.jinhx.blog.entity.video.Video;
import com.jinhx.blog.entity.video.dto.VideoDTO;
import com.jinhx.blog.entity.video.vo.HomeVideoInfoVO;
import com.jinhx.blog.entity.video.vo.VideoVO;
import com.jinhx.blog.mapper.video.VideoMapper;
import com.jinhx.blog.service.cache.CacheServer;
import com.jinhx.blog.service.operation.CategoryMapperService;
import com.jinhx.blog.service.operation.RecommendMapperService;
import com.jinhx.blog.service.operation.TagLinkMapperService;
import com.jinhx.blog.service.operation.TagMapperService;
import com.jinhx.blog.service.sys.SysUserMapperService;
import com.jinhx.blog.service.video.VideoMapperService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * VideoServiceImpl
 * @author jinhx
 * @date 2018/11/21 12:48
 * @description
 */
@Service
public class VideoMapperServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoMapperService {

    // 每天重新计算点赞，key
    private static final String BLOG_VIDEO_LIKE_LOCK_KEY = "blog:video:like:lock:";

    @Autowired
    private TagMapperService tagMapperService;

    @Autowired
    private TagLinkMapperService tagLinkMapperService;

    @Autowired
    private CategoryMapperService categoryMapperService;

    @Autowired
    private RecommendMapperService recommendMapperService;

    @Autowired
    private CacheServer cacheServer;

    @Resource
    private RabbitMQUtils rabbitmqUtils;

    @Resource
    private RedisUtils redisUtils;

    @Autowired
    private SysUserMapperService sysUserMapperService;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * 获取首页信息
     *
     * @return 首页信息
     */
    @Override
    public HomeVideoInfoVO getHommeVideoInfoVO() {
        Integer publishCount = baseMapper.selectPublishCount();
        Integer allCount = baseMapper.selectAllCount();

        HomeVideoInfoVO homeVideoInfoVO = new HomeVideoInfoVO();
        homeVideoInfoVO.setPublishCount(publishCount);
        homeVideoInfoVO.setAllCount(allCount);
        return homeVideoInfoVO;
    }

    /**
     * 分页查询视频列表
     * @param page 页码
     * @param limit 页数
     * @param title 标题
     * @return 视频列表
     */
    @Override
    public PageUtils queryPage(Integer page, Integer limit, String title) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        params.put("title", title);

        Page<VideoDTO> videoDTOPage = new Query<VideoDTO>(page, limit).getPage();
        List<VideoDTO> videoDTOList = baseMapper.listVideoDTO(videoDTOPage, params);
        // 查询所有分类
        List<Category> categoryList = categoryMapperService.list(new QueryWrapper<Category>().lambda().eq(Category::getModule, ModuleTypeConstants.VIDEO));
        // 封装ArticleVo
        List<VideoVO> videoVOList = new ArrayList<>();
        Optional.ofNullable(videoDTOList).ifPresent((videoDTOs ->
                videoDTOs.forEach(videoDTO -> {
                    // 设置类别
                    videoDTO.setCategoryListStr(categoryMapperService.renderCategoryArr(videoDTO.getCategoryId(), categoryList));
                    // 设置标签列表
                    List<TagLink> tagLinks = tagLinkMapperService.listTagLinks(videoDTO.getId(), ModuleTypeConstants.VIDEO);
                    if (!CollectionUtils.isEmpty(tagLinks)){
                        videoDTO.setTagList(tagMapperService.listByLinkId(tagLinks));
                    }

                    VideoVO videoVO = new VideoVO();
                    BeanUtils.copyProperties(videoDTO, videoVO);
                    if (recommendMapperService.selectRecommendByLinkIdAndType(videoDTO.getId(), ModuleTypeConstants.VIDEO) != null) {
                        videoVO.setRecommend(true);
                    } else {
                        videoVO.setRecommend(false);
                    }

                    videoVO.setAuthor(sysUserMapperService.getNicknameByUserId(videoVO.getCreaterId()));

                    videoVOList.add(videoVO);
                })));
        Page<VideoVO> videoVOPage = new Page<>();
        BeanUtils.copyProperties(videoDTOPage, videoVOPage);
        videoVOPage.setRecords(videoVOList);
        return new PageUtils(videoVOPage);
    }

    /**
     * 保存视频
     *
     * @param videoVO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveVideo(VideoVO videoVO) {
        baseMapper.insert(videoVO);
        videoVO.getTagList().forEach(item -> {
            tagMapperService.saveTagAndNew(item);
            TagLink tagLink = new TagLink();
            tagLink.setLinkId(videoVO.getId());
            tagLink.setTagId(item.getId());
            tagLink.setModule(ModuleTypeConstants.VIDEO);
            tagLinkMapperService.save(tagLink);
        });
        InitGitalkRequest initGitalkRequest = new InitGitalkRequest();
        initGitalkRequest.setId(videoVO.getId());
        initGitalkRequest.setTitle(videoVO.getTitle());
        initGitalkRequest.setType(GitalkConstants.GITALK_TYPE_VIDEO);
        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_GITALK_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_GITALK_INIT_ROUTINGKEY, JsonUtils.objectToJson(initGitalkRequest));
        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_VIDEO_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_VIDEO_ADD_ROUTINGKEY, JsonUtils.objectToJson(videoVO));

        cleanVideosCache(new Integer[]{});
    }

    /**
     * 更新视频
     *
     * @param videoVO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVideo(VideoVO videoVO) {
        // 删除多对多所属标签
        tagLinkMapperService.deleteTagLink(videoVO.getId(), ModuleTypeConstants.VIDEO);
        // 更新所属标签
        videoVO.getTagList().forEach(item -> {
            tagMapperService.saveTagAndNew(item);
            TagLink tagLink = new TagLink();
            tagLink.setLinkId(videoVO.getId());
            tagLink.setTagId(item.getId());
            tagLink.setModule(ModuleTypeConstants.VIDEO);
            tagLinkMapperService.save(tagLink);
        });
        // 更新博文
        baseMapper.updateVideoById(videoVO);
        if (videoVO.getRecommend() != null){
            if (videoVO.getRecommend()){
                if (recommendMapperService.selectRecommendByLinkIdAndType(videoVO.getId(), ModuleTypeConstants.VIDEO) == null){
                    Integer maxOrderNum = recommendMapperService.selectRecommendMaxOrderNum();
                    Recommend recommend = new Recommend();
                    recommend.setModule(ModuleTypeConstants.VIDEO);
                    recommend.setLinkId(videoVO.getId());
                    recommend.setOrderNum(maxOrderNum + 1);
                    recommendMapperService.insertRecommend(recommend);
                }
            }else {
                if (recommendMapperService.selectRecommendByLinkIdAndType(videoVO.getId(), ModuleTypeConstants.VIDEO) != null){
                    recommendMapperService.deleteRecommendsByLinkIdsAndType(Arrays.asList(videoVO.getId()), ModuleTypeConstants.VIDEO);
                }
            }
        }
        // 发送rabbitmq消息同步到es
        InitGitalkRequest initGitalkRequest = new InitGitalkRequest();
        initGitalkRequest.setId(videoVO.getId());
        initGitalkRequest.setTitle(videoVO.getTitle());
        initGitalkRequest.setType(GitalkConstants.GITALK_TYPE_VIDEO);
        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_GITALK_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_GITALK_INIT_ROUTINGKEY, JsonUtils.objectToJson(initGitalkRequest));
        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_VIDEO_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_VIDEO_UPDATE_ROUTINGKEY, JsonUtils.objectToJson(videoVO));

        cleanVideosCache(new Integer[]{videoVO.getId()});
    }

    /**
     * 更新视频状态
     *
     * @param videoVO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVideoStatus(VideoVO videoVO) {
        if (videoVO.getPublish() != null){
            // 更新发布状态
            baseMapper.updateVideoById(videoVO);
            if (videoVO.getPublish()){
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_VIDEO_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_VIDEO_ADD_ROUTINGKEY, JsonUtils.objectToJson(baseMapper.selectVideoById(videoVO.getId())));
            }else {
                Integer[] ids = {videoVO.getId()};
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_VIDEO_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_VIDEO_DELETE_ROUTINGKEY, JsonUtils.objectToJson(ids));
            }
        }
        if (videoVO.getRecommend() != null){
            // 更新推荐状态
            if (videoVO.getRecommend()){
                if (recommendMapperService.selectRecommendByLinkIdAndType(videoVO.getId(), ModuleTypeConstants.VIDEO) == null){
                    Integer maxOrderNum = recommendMapperService.selectRecommendMaxOrderNum();
                    Recommend recommend = new Recommend();
                    recommend.setModule(ModuleTypeConstants.VIDEO);
                    recommend.setLinkId(videoVO.getId());
                    recommend.setOrderNum(maxOrderNum + 1);
                    recommendMapperService.insertRecommend(recommend);
                }
            }else {
                if (recommendMapperService.selectRecommendByLinkIdAndType(videoVO.getId(), ModuleTypeConstants.VIDEO) != null){
                    recommendMapperService.deleteRecommendsByLinkIdsAndType(Arrays.asList(videoVO.getId()), ModuleTypeConstants.VIDEO);
                }
            }
        }

        cleanVideosCache(new Integer[]{videoVO.getId()});
    }

    /**
     * 获取视频对象
     *
     * @param videoId videoId
     * @param publish publish
     * @return VideoVO
     */
    @Override
    public VideoVO getVideoVO(Integer videoId, Boolean publish) {
        VideoVO videoVO = new VideoVO();
        Video video = getVideo(videoId, publish);
        BeanUtils.copyProperties(video, videoVO);
        // 查询所属标签
        List<TagLink> tagLinks = tagLinkMapperService.listTagLinks(videoId, ModuleTypeConstants.VIDEO);
        if (!CollectionUtils.isEmpty(tagLinks)){
            videoVO.setTagList(tagMapperService.listByLinkId(tagLinks));
        }
        Recommend recommend = recommendMapperService.selectRecommendByLinkIdAndType(videoId, ModuleTypeConstants.VIDEO);
        if (recommend != null){
            videoVO.setRecommend(true);
        }else {
            videoVO.setRecommend(false);
        }

        videoVO.setAuthor(sysUserMapperService.getNicknameByUserId(videoVO.getCreaterId()));

        return videoVO;
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
     * @param categoryId
     * @return
     */
    @Override
    public boolean checkByCategory(Integer categoryId) {
        return baseMapper.checkByCategory(categoryId) > 0;
    }

    /**
     * 判断上传文件下是否有文章
     * @param url
     * @return
     */
    @Override
    public boolean checkByFile(String url) {
        return baseMapper.checkByFile(url) > 0;
    }

    /**
     * 批量删除
     *
     * @param ids 文章id数组
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVideos(Integer[] ids) {
        //先删除博文标签多对多关联
        Arrays.stream(ids).forEach(videoId -> {
            tagLinkMapperService.deleteTagLink(videoId, ModuleTypeConstants.VIDEO);
        });
        baseMapper.deleteBatchIds(Arrays.asList(ids));

        recommendMapperService.deleteRecommendsByLinkIdsAndType(Arrays.asList(ids), ModuleTypeConstants.VIDEO);
        // 发送rabbitmq消息同步到es
        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_VIDEO_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_VIDEO_DELETE_ROUTINGKEY, JsonUtils.objectToJson(ids));

        cleanVideosCache(ids);
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

    /**
     * 清除缓存
     */
    private void cleanVideosCache(Integer[] ids){
        taskExecutor.execute(() ->{
            cacheServer.cleanVideosCache(ids);
        });
    }

    /********************** portal ********************************/

    /**
     * 分页获取视频列表
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
    public PageUtils listVideos(Integer page, Integer limit, Boolean latest, Integer categoryId, Boolean like, Boolean watch) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        params.put("latest", latest);
        params.put("like", like);
        params.put("watch", watch);
        if (categoryId != null){
            params.put("categoryId", String.valueOf(categoryId));
        }

        Page<VideoDTO> videoDTOPage = new Query<VideoDTO>(page, limit).getPage();
        List<VideoDTO> videoDTOList = baseMapper.queryPageCondition(videoDTOPage, params);
        if (videoDTOList == null){
            videoDTOList = new ArrayList<>();
        }

        videoDTOList.forEach(videoDTOListItem -> {
            videoDTOListItem.setAuthor(sysUserMapperService.getNicknameByUserId(videoDTOListItem.getCreaterId()));
        });

        videoDTOPage.setRecords(videoDTOList);
        return new PageUtils(videoDTOPage);
    }

    /**
     * 获取VideoDTO对象
     * @param id id
     * @return VideoDTO
     */
    @Cacheable(value = RedisKeyConstants.VIDEO, key = "#id")
    @Override
    public VideoDTO getVideoDTO(Integer id) {
        Video video = baseMapper.selectVideoById(id);
        if (video == null){
            return null;
        }
        VideoDTO videoDTO = new VideoDTO();
        BeanUtils.copyProperties(video, videoDTO);
        videoDTO.setAuthor(sysUserMapperService.getNicknameByUserId(videoDTO.getCreaterId()));
        List<TagLink> tagLinks = tagLinkMapperService.listTagLinks(id, ModuleTypeConstants.VIDEO);
        if (!CollectionUtils.isEmpty(tagLinks)){
            videoDTO.setTagList(tagMapperService.listByLinkId(tagLinks));
        }
        // 观看数量
        baseMapper.updateWatchNum(id);
        return videoDTO;
    }

    /**
     * 获取热观榜
     * @return 热观视频列表
     */
    @Cacheable(value = RedisKeyConstants.VIDEOS, key = "'hotwatch'")
    @Override
    public List<VideoVO> listHotWatchVideos() {
        List<VideoDTO> videoDTOList = baseMapper.getHotWatchList();
        List<VideoVO> videoVOList = new ArrayList<>();
        videoDTOList.forEach(videoDTOListItem -> {
            VideoVO videoVO = new VideoVO();
            BeanUtils.copyProperties(videoDTOListItem, videoVO);
            videoVOList.add(videoVO);
        });
        return videoVOList;
    }

    /**
     * 视频点赞
     * @param id id
     * @return 点赞结果
     */
    @Override
    public Boolean updateVideo(Integer id) throws Exception {
        //获取request
        ParamsHttpServletRequestWrapper request = (ParamsHttpServletRequestWrapper) HttpContextUtils.getHttpServletRequest();
        String userId = EncodeUtils.encoderByMD5(IPUtils.getIpAddr(request) + UserAgentUtils.getBrowserName(request) +
                UserAgentUtils.getBrowserVersion(request) + UserAgentUtils.getDeviceManufacturer(request) +
                UserAgentUtils.getDeviceType(request) + UserAgentUtils.getOsVersion(request));
        // 每天重新计算点赞
        if (!redisUtils.setIfAbsent(BLOG_VIDEO_LIKE_LOCK_KEY + userId + ":" + id, "1", DateUtils.getRemainMilliSecondsOneDay())){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "1天只能点赞1次，请明天再来点赞");
        }

        return baseMapper.updateLikeNum(id);
    }

}
