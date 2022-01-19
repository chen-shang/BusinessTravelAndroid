package com.business.travel.app.utils;

import com.business.travel.app.constant.ThreadPoolConstant;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * @author chenshang
 * 异步任务工具类
 */
public class FutureUtil {
    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, ThreadPoolConstant.COMMON_THREAD_POOL);
    }

    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
        return CompletableFuture.supplyAsync(supplier, ThreadPoolConstant.COMMON_THREAD_POOL);
    }
}
