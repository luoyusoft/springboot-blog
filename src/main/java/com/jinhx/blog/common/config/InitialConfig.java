package com.jinhx.blog.common.config;

import com.jinhx.blog.common.util.RabbitMQUtils;
import org.elasticsearch.client.ElasticsearchClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * InitialConfig
 *
 * @author jinhx
 * @since 2020-10-07
 */
@Configuration
@ConditionalOnBean(ElasticsearchClient.class)
public class InitialConfig {

    @Resource
    private RabbitMQUtils rabbitmqUtils;

    /**
     * 项目启动时重新导入索引
     */
    @PostConstruct
    public void initEsIndex(){
//        rabbitmqUtils.send(RabbitMqConstants.BLOG_INIT_ES_QUEUE,"blog-search init index");
//        ConnectionFactory connectionFactory = new ConnectionFactory();
//        connectionFactory.setAutomaticRecoveryEnabled(false);
    }

}
