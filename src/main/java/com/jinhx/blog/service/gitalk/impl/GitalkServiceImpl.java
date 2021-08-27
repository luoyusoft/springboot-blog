package com.jinhx.blog.service.gitalk.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.jinhx.blog.common.api.GitalkApi;
import com.jinhx.blog.common.constants.GitalkConstants;
import com.jinhx.blog.common.constants.RabbitMQConstants;
import com.jinhx.blog.common.util.JsonUtils;
import com.jinhx.blog.common.util.RabbitMQUtils;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.gitalk.InitGitalkRequest;
import com.jinhx.blog.entity.video.Video;
import com.jinhx.blog.service.article.ArticleMapperService;
import com.jinhx.blog.service.gitalk.GitalkService;
import com.jinhx.blog.service.video.VideoMapperService;
import com.rabbitmq.client.Channel;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * GitalkServiceImpl
 *
 * @author jinhx
 * @since 2020-11-07
 */
@Slf4j
@Service
public class GitalkServiceImpl implements GitalkService {

    @Autowired
    private GitalkApi gitalkApi;

    @Resource
    private RabbitMQUtils rabbitmqUtils;

    @Autowired
    private ArticleMapperService articleMapperService;

    @Autowired
    private VideoMapperService videoMapperService;

    /**
     * 初始化gitalk文章数据
     *
     * @return 初始化结果
     */
    @Override
    public boolean initArticleList(){
        List<Article> articles = articleMapperService.selectArticlesByPublish(Article.PUBLISH_TRUE);

        XxlJobLogger.log("初始化gitalk文章数据，查到个数：{}", articles.size());
        log.info("初始化gitalk文章数据，查到个数：{}", articles.size());
        if (CollectionUtils.isNotEmpty(articles)){
            articles.forEach(x -> {
                InitGitalkRequest initGitalkRequest = new InitGitalkRequest();
                initGitalkRequest.setId(x.getArticleId());
                initGitalkRequest.setType(GitalkConstants.GITALK_TYPE_ARTICLE);
                initGitalkRequest.setTitle(x.getTitle());
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_GITALK_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_GITALK_INIT_ROUTINGKEY, JsonUtils.objectToJson(initGitalkRequest));
            });
        }
        return true;
    }

    /**
     * 初始化gitalk视频数据
     *
     * @return 初始化结果
     */
    @Override
    public boolean initVideoList(){
        List<Video> videos = videoMapperService.selectVideosByPublish(Video.PUBLISH_TRUE);
        XxlJobLogger.log("初始化gitalk视频数据，查到个数：{}", videos.size());
        log.info("初始化gitalk视频数据，查到个数：{}", videos.size());
        if (CollectionUtils.isNotEmpty(videos)){
            videos.forEach(x -> {
                InitGitalkRequest initGitalkRequest = new InitGitalkRequest();
                initGitalkRequest.setId(x.getVideoId());
                initGitalkRequest.setType(GitalkConstants.GITALK_TYPE_VIDEO);
                initGitalkRequest.setTitle(x.getTitle());
                rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_GITALK_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_GITALK_INIT_ROUTINGKEY, JsonUtils.objectToJson(initGitalkRequest));
            });
        }
        return true;
    }

    /**
     * RabbitMQ消费者
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitMQConstants.BLOG_GITALK_INIT_QUEUE, durable = "true"),
            exchange = @Exchange(
                    value = RabbitMQConstants.BLOG_GITALK_TOPIC_EXCHANGE,
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {RabbitMQConstants.TOPIC_GITALK_ROUTINGKEY}))
    public void initGitalkConsumer(Message message, Channel channel){
        try {
            InitGitalkRequest initGitalkRequest = JsonUtils.jsonToObject(new String(message.getBody()), InitGitalkRequest.class);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            if (gitalkApi.initArticle(initGitalkRequest)){
                //手动确认消息已经被消费
                log.info("新增或更新标题，进行Gitalk初始化：" + JsonUtils.objectToJson(message) + "成功！");
            }else {
                log.info("新增或更新标题，进行Gitalk初始化：" + JsonUtils.objectToJson(message) + "失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("新增或更新标题，进行Gitalk初始化：" + JsonUtils.objectToJson(message) + "失败！");
            log.info("手动确认Gitalk初始化消息已经被消费失败！");
        }
    }

}
