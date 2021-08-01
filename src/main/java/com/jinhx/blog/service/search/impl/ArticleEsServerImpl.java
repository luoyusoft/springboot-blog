package com.jinhx.blog.service.search.impl;

import com.jinhx.blog.adaptor.article.ArticleAdaptor;
import com.jinhx.blog.adaptor.article.ArticleAdaptorBuilder;
import com.jinhx.blog.common.constants.ElasticSearchConstants;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.constants.RabbitMQConstants;
import com.jinhx.blog.common.util.ElasticSearchUtils;
import com.jinhx.blog.common.util.JsonUtils;
import com.jinhx.blog.common.util.RabbitMQUtils;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.service.article.ArticleService;
import com.jinhx.blog.service.operation.TagService;
import com.jinhx.blog.service.search.ArticleEsServer;
import com.jinhx.blog.service.sys.SysUserService;
import com.rabbitmq.client.Channel;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
 * ArticleEsServerImpl
 *
 * @author jinhx
 * @since 2019-04-11
 */
@Slf4j
@Service
public class ArticleEsServerImpl implements ArticleEsServer {

    @Autowired
    private ElasticSearchUtils elasticSearchUtils;

    @Resource
    private RabbitMQUtils rabbitmqUtils;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private TagService tagService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ArticleAdaptor articleAdaptor;

    /**
     * 初始化es文章数据
     *
     * @return 初始化结果
     */
    @Override
    public boolean initArticleList() throws Exception {
        if(elasticSearchUtils.deleteIndex(ElasticSearchConstants.BLOG_SEARCH_ARTICLE_INDEX)){
            if(elasticSearchUtils.createIndex(ElasticSearchConstants.BLOG_SEARCH_ARTICLE_INDEX)){
                List<Article> articles = articleService.listArticlesByPublish();
                XxlJobLogger.log("初始化es文章数据，查到个数：{}", articles.size());
                log.info("初始化es文章数据，查到个数：{}", articles.size());
                if(!CollectionUtils.isEmpty(articles)){
                    articles.forEach(x -> {
                        ArticleVO articleVO = articleAdaptor.adaptorArticleToArticleVO(new ArticleAdaptorBuilder.Builder<Article>()
                                .setAuthor()
                                .build(x));

                        rabbitmqUtils.sendByRoutingKey(RabbitMQConstants.BLOG_ARTICLE_TOPIC_EXCHANGE, RabbitMQConstants.TOPIC_ES_ARTICLE_ADD_ROUTINGKEY, JsonUtils.objectToJson(articleVO));
                    });
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 新增文章，rabbitmq监听器，添加到es中
     *
     * @param message message
     * @param channel channel
     */
    @RabbitListener(queues = RabbitMQConstants.BLOG_ES_ARTICLE_ADD_QUEUE)
    public void addListener(Message message, Channel channel){
        try {
            //手动确认消息已经被消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            if(message != null && message.getBody() != null){
                ArticleVO articleVO = JsonUtils.jsonToObject(new String(message.getBody()), ArticleVO.class);
                elasticSearchUtils.addDocument(ElasticSearchConstants.BLOG_SEARCH_ARTICLE_INDEX, articleVO.getId().toString(), JsonUtils.objectToJson(articleVO));
                log.info("新增文章，rabbitmq监听器，添加到es中成功：id:" + new String(message.getBody()));
            }else {
                log.info("新增文章，rabbitmq监听器，添加到es中失败：article:" + new String(message.getBody()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("新增文章，rabbitmq监听器，手动确认消息已经被消费失败，article:" + new String(message.getBody()));
        }
    }

    /**
     * 更新文章，rabbitmq监听器，更新到es
     *
     * @param message message
     * @param channel channel
     */
    @RabbitListener(queues = RabbitMQConstants.BLOG_ES_ARTICLE_UPDATE_QUEUE)
    public void updateListener(Message message, Channel channel){
        try {
            //手动确认消息已经被消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            if(message != null && message.getBody() != null){
                ArticleVO articleVO = JsonUtils.jsonToObject(new String(message.getBody()), ArticleVO.class);
                elasticSearchUtils.updateDocument(ElasticSearchConstants.BLOG_SEARCH_ARTICLE_INDEX, articleVO.getId().toString(), JsonUtils.objectToJson(articleVO));
                log.info("更新文章，rabbitmq监听器，更新到es成功：id:" + new String(message.getBody()));
            }else {
                log.info("更新文章，rabbitmq监听器，更新到es失败：article:" + new String(message.getBody()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("更新文章，rabbitmq监听器，手动确认消息已经被消费失败，article:" + new String(message.getBody()));
        }
    }

    /**
     * 删除文章，rabbitmq监听器，从es中删除
     *
     * @param message message
     * @param channel channel
     */
    @RabbitListener(queues = RabbitMQConstants.BLOG_ES_ARTICLE_DELETE_QUEUE)
    public void deleteListener(Message message, Channel channel){
        try {
            //手动确认消息已经被消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            StringBuffer articleIdsS = new StringBuffer();
            if(message != null && message.getBody() != null){
                Integer[] articleIds = JsonUtils.jsonToObject(new String(message.getBody()), Integer[].class);
                for (int i = 0; i < articleIds.length; i++) {
                    elasticSearchUtils.deleteDocument(ElasticSearchConstants.BLOG_SEARCH_ARTICLE_INDEX, articleIds[i].toString());
                    articleIdsS.append(articleIds[i] + ",");
                }
                log.info("删除文章，rabbitmq监听器，从es中删除成功：id:" + articleIdsS);
            }else {
                log.info("删除文章，rabbitmq监听器，从es中删除失败：article:" + articleIdsS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            StringBuffer articleIdsS = null;
            Integer[] articleIds = JsonUtils.jsonToObject(new String(message.getBody()), Integer[].class);
            for (int i = 0; i < articleIds.length; i++) {
                articleIdsS.append(i + ",");
            }
            log.info("删除文章，rabbitmq监听器，手动确认消息已经被消费失败，article:" + articleIdsS);
        }
    }

    /**
     * 搜索文章
     *
     * @param keyword 关键字
     * @return 搜索结果
     */
    @Override
    public List<ArticleVO> searchArticleList(String keyword) throws Exception {
        List<String> highlightBuilderList = Arrays.asList("title", "description");
        List<Map<String, Object>> searchRequests = elasticSearchUtils.searchRequest(ElasticSearchConstants.BLOG_SEARCH_ARTICLE_INDEX, keyword, highlightBuilderList, highlightBuilderList);
        List<ArticleVO> articleVOList = new ArrayList<>();
        for(Map<String, Object> x : searchRequests){
            ArticleVO articleVO = new ArticleVO();
            articleVO.setId(Integer.valueOf(x.get("id").toString()));
            articleVO.setCover(x.get("cover").toString());
            articleVO.setCoverType(Integer.valueOf(x.get("coverType").toString()));
            articleVO.setCreateTime(LocalDateTime.parse(x.get("createTime").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            articleVO.setUpdateTime(LocalDateTime.parse(x.get("updateTime").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            articleVO.setReadNum(Long.valueOf(x.get("readNum").toString()));
            articleVO.setTitle(x.get("title").toString());
            articleVO.setOpen(Boolean.valueOf(x.get("open").toString()));
            articleVO.setAuthor(x.get("author").toString());
            articleVO.setDescription(x.get("description").toString());
            articleVO.setLikeNum(Long.valueOf(x.get("likeNum").toString()));
            articleVO.setTop(false);
            articleVO.setTagList(tagService.listByLinkId(articleVO.getId(), ModuleTypeConstants.ARTICLE));
            articleVOList.add(articleVO);
        }
        return articleVOList;
    }

}
