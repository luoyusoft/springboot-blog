package com.jinhx.blog.service.search.impl;

import com.jinhx.blog.common.constants.ElasticSearchConstants;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.constants.RabbitMQConstants;
import com.jinhx.blog.common.util.ElasticSearchUtils;
import com.jinhx.blog.common.util.JsonUtils;
import com.jinhx.blog.common.util.RabbitMQUtils;
import com.jinhx.blog.entity.operation.TagLink;
import com.jinhx.blog.entity.video.dto.VideoDTO;
import com.jinhx.blog.entity.video.vo.VideoVO;
import com.jinhx.blog.service.operation.TagLinkMapperService;
import com.jinhx.blog.service.operation.TagMapperService;
import com.jinhx.blog.service.sys.SysUserMapperService;
import com.jinhx.blog.mapper.video.VideoMapper;
import com.jinhx.blog.service.search.VideoEsServer;
import com.rabbitmq.client.Channel;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * VideoEsServerImpl
 *
 * @author jinhx
 * @since 2019-04-11
 */
@Slf4j
@Service
public class VideoEsServerImpl implements VideoEsServer {

    @Autowired
    private ElasticSearchUtils elasticSearchUtils;

    @Resource
    private RabbitMQUtils rabbitmqUtils;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private TagMapperService tagMapperService;

    @Autowired
    private TagLinkMapperService tagLinkMapperService;

    @Autowired
    private SysUserMapperService sysUserMapperService;

    /**
     * 初始化es视频数据
     *
     * @return 初始化结果
     */
    @Override
    public boolean initVideoList() throws Exception {
        if(elasticSearchUtils.deleteIndex(ElasticSearchConstants.BLOG_SEARCH_VIDEO_INDEX)){
            if(elasticSearchUtils.createIndex(ElasticSearchConstants.BLOG_SEARCH_VIDEO_INDEX)){
                List<VideoDTO> videoDTOList = videoMapper.selectVideoDTOList();
                XxlJobLogger.log("初始化es视频数据，查到个数：{}", videoDTOList.size());
                log.info("初始化es视频数据，查到个数：{}", videoDTOList.size());
                if(!CollectionUtils.isEmpty(videoDTOList)){
                    videoDTOList.forEach(x -> {
                        VideoVO videoVO = new VideoVO();
                        BeanUtils.copyProperties(x, videoVO);
                        videoVO.setAuthor(sysUserMapperService.getNicknameByUserId(videoVO.getCreaterId()));
                        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_VIDEO_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_VIDEO_ADD_ROUTINGKEY, JsonUtils.objectToJson(videoVO));
                    });
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 新增视频，rabbitmq监听器，添加到es中
     *
     * @param message message
     * @param channel channel
     */
    @RabbitListener(queues = RabbitMQConstants.BLOG_ES_VIDEO_ADD_QUEUE)
    public void addListener(Message message, Channel channel){
        try {
            //手动确认消息已经被消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            if(message.getBody() != null){
                VideoVO videoVO = JsonUtils.jsonToObject(new String(message.getBody()), VideoVO.class);
                if (videoVO != null){
                    elasticSearchUtils.addDocument(ElasticSearchConstants.BLOG_SEARCH_VIDEO_INDEX, videoVO.getId().toString(), JsonUtils.objectToJson(videoVO));
                    log.info("新增视频，rabbitmq监听器，添加到es中成功：id:" + new String(message.getBody()));
                }
            }else {
                log.info("新增视频，rabbitmq监听器，添加到es中失败：video:" + new String(message.getBody()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("新增视频，rabbitmq监听器，手动确认消息已经被消费失败，video:" + new String(message.getBody()));
        }
    }

    /**
     * 更新视频，rabbitmq监听器，更新到es
     *
     * @param message message
     * @param channel channel
     */
    @RabbitListener(queues = RabbitMQConstants.BLOG_ES_VIDEO_UPDATE_QUEUE)
    public void updateListener(Message message, Channel channel){
        try {
            //手动确认消息已经被消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            if(message.getBody() != null){
                VideoVO videoVO = JsonUtils.jsonToObject(new String(message.getBody()), VideoVO.class);
                if (videoVO != null){
                    elasticSearchUtils.updateDocument(ElasticSearchConstants.BLOG_SEARCH_VIDEO_INDEX, videoVO.getId().toString(), JsonUtils.objectToJson(videoVO));
                    log.info("更新视频，rabbitmq监听器，更新到es成功：id:" + new String(message.getBody()));
                }
            }else {
                log.info("更新视频，rabbitmq监听器，更新到es失败：video:" + new String(message.getBody()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("更新视频，rabbitmq监听器，手动确认消息已经被消费失败，video:" + new String(message.getBody()));
        }
    }

    /**
     * 删除视频，rabbitmq监听器，从es中删除
     *
     * @param message message
     * @param channel channel
     */
    @RabbitListener(queues = RabbitMQConstants.BLOG_ES_VIDEO_DELETE_QUEUE)
    public void deleteListener(Message message, Channel channel){
        try {
            //手动确认消息已经被消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            StringBuffer videoIdsS = new StringBuffer();
            if(message.getBody() != null){
                Integer[] videoIds = JsonUtils.jsonToObject(new String(message.getBody()), Integer[].class);
                for (int i = 0; i < videoIds.length; i++) {
                    elasticSearchUtils.deleteDocument(ElasticSearchConstants.BLOG_SEARCH_VIDEO_INDEX, videoIds[i].toString());
                    videoIdsS.append(videoIds[i] + ",");
                }
                log.info("删除视频，rabbitmq监听器，从es中删除成功：id:" + videoIdsS);
            }else {
                log.info("删除视频，rabbitmq监听器，从es中删除失败：video:" + videoIdsS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            StringBuffer articleIdsS = null;
            Integer[] articleIds = JsonUtils.jsonToObject(new String(message.getBody()), Integer[].class);
            for (int i = 0; i < articleIds.length; i++) {
                articleIdsS.append(i + ",");
            }
            log.info("删除视频，rabbitmq监听器，手动确认消息已经被消费失败，video:" + articleIdsS);
        }
    }

    /**
     * 搜索视频
     *
     * @param keyword 关键字
     * @return 搜索结果
     */
    @Override
    public List<VideoVO> searchVideoList(String keyword) throws Exception {
        List<String> highlightBuilderList = Arrays.asList("title", "alternateName");
        List<Map<String, Object>> searchRequests = elasticSearchUtils.searchRequest(ElasticSearchConstants.BLOG_SEARCH_VIDEO_INDEX, keyword, highlightBuilderList, highlightBuilderList);
        List<VideoVO> videoVOList = new ArrayList<>();
        for(Map<String, Object> x : searchRequests){
            VideoVO videoVO = new VideoVO();
            videoVO.setId(Integer.valueOf(x.get("id").toString()));
            videoVO.setCover(x.get("cover").toString());
            videoVO.setVideoUrl(x.get("videoUrl").toString());
            videoVO.setAlternateName(x.get("alternateName").toString());
            videoVO.setScore(x.get("score").toString());
            videoVO.setCreateTime(LocalDateTime.parse(x.get("createTime").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            videoVO.setUpdateTime(LocalDateTime.parse(x.get("updateTime").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            videoVO.setWatchNum(Long.valueOf(x.get("watchNum").toString()));
            videoVO.setTitle(x.get("title").toString());
            videoVO.setAuthor(x.get("author").toString());
            videoVO.setSynopsis(x.get("synopsis").toString());
            videoVO.setLikeNum(Long.valueOf(x.get("likeNum").toString()));
            videoVO.setTop(false);
            List<TagLink> tagLinks = tagLinkMapperService.listTagLinks(videoVO.getId(), ModuleTypeConstants.VIDEO);
            if (!CollectionUtils.isEmpty(tagLinks)){
                videoVO.setTagList(tagMapperService.listByLinkId(tagLinks));
            }
            videoVOList.add(videoVO);
        }
        return videoVOList;
    }

}
