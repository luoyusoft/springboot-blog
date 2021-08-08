package com.jinhx.blog.engine.article;

import com.jinhx.blog.common.util.NacosUtils;
import com.jinhx.blog.entity.base.BaseRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * ArticleNode
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Slf4j
public abstract class ArticleNode<T extends BaseRequestDTO> {

    private final String LOG_END  = " execute success name=";
    private final String LOG_SKIP  = " skip=";
    private final String LOG_TIME  = " time=";
    private final String TRUE  = "true";
    private final String FALSE  = "false";
    private final String LOG_STR_ENTER = "\r\n";

    /**
     * 节点执行方法
     *
     * @param context context
     */
    protected abstract void process(ArticleQueryContextInfo<T> context);

    /**
     * 通用执行方法
     *
     * @param context context
     */
    public void execute(ArticleQueryContextInfo<T> context) {
        String logStr = context.getQueryDTO().getLogStr();
        try {
            // 日志
            StringBuilder logInfo = new StringBuilder(logStr);
            String processorName = getProcessorName();

            buildLogInfo(logInfo, Arrays.asList(LOG_END, processorName));

            // 耗时计算
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            if (isSkip(context)) {
                buildLogInfo(logInfo, Arrays.asList(LOG_SKIP, TRUE));
            } else {
                buildLogInfo(logInfo, Arrays.asList(LOG_SKIP, FALSE));
                process(context);
            }

            stopWatch.stop();
            long time = stopWatch.getTime();

            buildLogInfo(logInfo, Arrays.asList(LOG_TIME, time, LOG_STR_ENTER));

            log.info(logInfo.toString());
        } catch (Exception e) {
            log.error(logStr + " execute fail name={} msg={}", getProcessorName(), ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    /**
     * 日志量控制 可能出现YGC频繁
     *
     * @param logInfo logInfo
     * @param logInfos logInfos
     */
    private void buildLogInfo(StringBuilder logInfo, List<Object> logInfos) {
        if(NacosUtils.getMDCLogSwitch()){
            logInfos.forEach(logInfo::append);
        }
    }

    /**
     * 获取当前执行节点名称
     *
     * @return 当前执行节点名称
     */
    protected abstract String getProcessorName();

    /**
     * 是否跳过当前执行方法，默认不跳过
     *
     * @return 是否跳过当前执行方法
     */
    protected boolean isSkip(ArticleQueryContextInfo<T> context) {
        return false;
    }

}
