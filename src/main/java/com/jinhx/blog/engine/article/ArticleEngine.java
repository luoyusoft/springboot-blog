package com.jinhx.blog.engine.article;

import com.google.common.collect.Lists;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.threadpool.ThreadPoolEnum;
import com.jinhx.blog.common.util.SpringUtils;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * ArticleEngine
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Slf4j
@Service
public class ArticleEngine {

    public <T extends BaseRequestDTO> void execute(ArticleNodeChain nodeChain, ArticleQueryContextInfo<T> context) {
        Map<String, List<Class<? extends ArticleNode>>> nodesMap = nodeChain.getNodesMap();

        for (Map.Entry<String, List<Class<? extends ArticleNode>>> nodesEntry : nodesMap.entrySet()) {
            List<Class<? extends ArticleNode>> nodeClasses = nodesEntry.getValue();
            if (nodeClasses.size() == 1) {
                ArticleNode<T> node = SpringUtils.getBean(nodeClasses.get(0));
                node.execute(context);
            } else {
                // 多个node节点的组合节点，并行执行
                List<Future> futureList = Lists.newArrayList();
                for (Class<? extends ArticleNode> nodeClass : nodeClasses) {
                    ArticleNode<T> node = SpringUtils.getBean(nodeClass);
                    futureList.add(ThreadPoolEnum.ARTICLE.getThreadPoolExecutor().submit(() -> {
                        node.execute(context);
                        return null;
                    }));
                }

                MyException myException = null;
                for (Future future : futureList) {
                    try {
                        // 超时处理
                        future.get(5000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("article engine execute error msg={}", ExceptionUtils.getStackTrace(e));
                        myException = new MyException(ResponseEnums.TIMEOUT);
                    }
                }

                // 是否需要执行下一组节点
                if(Objects.nonNull(context.getExNextNode()) && !context.getExNextNode()){
                    return;
                }

                if (myException != null) {
                    throw myException;
                }
            }
        }
    }

}
