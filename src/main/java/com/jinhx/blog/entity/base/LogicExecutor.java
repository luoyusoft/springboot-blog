package com.jinhx.blog.entity.base;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * LogicExecutor
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Slf4j
public abstract class LogicExecutor<T> {

    protected LogicExecutor() {
    }

    protected abstract void checkParams();

    protected abstract T process();

    protected abstract String getParams();

    protected abstract String getProcessorName();

    protected void afterProcess() {
    }

    protected void onSuccess() {
    }

    protected void onFail() {
    }

    public Response<T> execute() {
        return this.doExecute();
    }

    private Response<T> doExecute() {
        try {
            log.info("checkParams start act={} params={}", getProcessorName(), getParams());
            this.checkParams();
        } catch (Exception e) {
            log.error("checkParams fail act={} msg={}", getProcessorName(), ExceptionUtils.getStackTrace(e));
            throw e;
        }

        try {
            // 耗时计算
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            T result = this.process();

            stopWatch.stop();
            log.info("process success act={} time={} response={}", getProcessorName(), stopWatch.getTime(), result.toString());
            this.onSuccess();
            return Response.success(result);
        }catch (Throwable e) {
            this.onFail();
            log.error("process fail act={} msg={}", getProcessorName(), ExceptionUtils.getStackTrace(e));
            throw e;
        } finally {
            this.afterProcess();
        }
    }
}
