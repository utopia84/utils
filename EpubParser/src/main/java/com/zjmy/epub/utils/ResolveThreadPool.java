package com.zjmy.epub.utils;


import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义的下载线程池
 */
public class ResolveThreadPool {
    //CPU核心数
    //private int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //可同时处理的任务数（核心线程数）
    private final int CORE_POOL_SIZE = 2;
    //缓存队列的大小（最大线程数）
    private final int MAX_POOL_SIZE = 6;
    //非核心线程闲置的超时时间（秒），如果超时则会被回收
    private final int KEEP_ALIVE = 5;

    private ThreadPoolExecutor THREAD_POOL_EXECUTOR;

    private ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger();

        @Override
        public Thread newThread( Runnable runnable) {
            return new Thread(runnable, "resolver_task#" + mCount.getAndIncrement());
        }
    };

    private ResolveThreadPool() {
    }

    public static ResolveThreadPool getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final ResolveThreadPool instance = new ResolveThreadPool();
    }


    public ThreadPoolExecutor getThreadPoolExecutor() {
        if (THREAD_POOL_EXECUTOR == null) {
            THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
                    CORE_POOL_SIZE, MAX_POOL_SIZE,
                    KEEP_ALIVE, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(),
                    sThreadFactory);
        }
        return THREAD_POOL_EXECUTOR;
    }
}
