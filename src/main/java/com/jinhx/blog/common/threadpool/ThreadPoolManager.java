package com.jinhx.blog.common.threadpool;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadPoolManager
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Slf4j
public class ThreadPoolManager {

    private static final AtomicInteger ARTICLE_OPERATION_THREAD_POOL_COUNTER = new AtomicInteger(0);

    private static final AtomicInteger COMMON_OPERATION_THREAD_POOL_COUNTER = new AtomicInteger(0);

    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();

    /**
     * 异步文章操作线程池
     */
    public static final ThreadPoolExecutor ARTICLE_OPERATION_THREAD_POOL = new ThreadPoolExecutorMDCWrapper(
            2, CPU_NUM * 2,
            10, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(1000),
            (Runnable r) -> new Thread(r, "asyncArticleOperation_thread_" + ARTICLE_OPERATION_THREAD_POOL_COUNTER.incrementAndGet()),
            (r, executor) -> log.error("async article operation has bean rejected" + r));

    /**
     * 异步公共操作线程池
     */
    public static final ThreadPoolExecutor COMMON_OPERATION_THREAD_POOL = new ThreadPoolExecutorMDCWrapper(
            2, CPU_NUM * 2,
            10, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(1000),
            (Runnable r) -> new Thread(r, "asyncCommonOperation_thread_" + COMMON_OPERATION_THREAD_POOL_COUNTER.incrementAndGet()),
            (r, executor) -> log.error("async common operation has bean rejected" + r));

    public static final ListeningExecutorService ARTICLE_OPERATION_EXECUTE_SERVICE = MoreExecutors.listeningDecorator(ARTICLE_OPERATION_THREAD_POOL);

    public static final ListeningExecutorService COMMON_OPERATION_EXECUTE_SERVICE = MoreExecutors.listeningDecorator(COMMON_OPERATION_THREAD_POOL);

}
