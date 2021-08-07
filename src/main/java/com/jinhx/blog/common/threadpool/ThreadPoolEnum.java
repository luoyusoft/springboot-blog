package com.jinhx.blog.common.threadpool;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * ThreadPoolEnum
 *
 * @author jinhx
 * @since 2021-08-06
 */
public enum ThreadPoolEnum {

    /**
     * 异步文章操作线程池
     */
    ARTICLE(1, ThreadPoolManager.ARTICLE_OPERATION_THREAD_POOL),

    /**
     * 异步公共操作线程池
     */
    COMMON(2, ThreadPoolManager.COMMON_OPERATION_THREAD_POOL),;

    /**
     * 获取线程池
     *
     * @param code code
     * @return ThreadPoolExecutor
     */
    public static ThreadPoolExecutor getEnumByCode(int code){
        for(ThreadPoolEnum threadPoolEnum : ThreadPoolEnum.values()){
            if (code == threadPoolEnum.getCode()){
                return threadPoolEnum.getThreadPoolExecutor();
            }
        }
        return COMMON.threadPoolExecutor;
    }

    private int code;
    private ThreadPoolExecutor threadPoolExecutor;

    ThreadPoolEnum(int status, ThreadPoolExecutor threadPoolExecutor) {
        this.code = status;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public int getCode() {
        return code;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

}
