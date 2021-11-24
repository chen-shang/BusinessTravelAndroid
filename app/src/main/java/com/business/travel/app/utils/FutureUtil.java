package com.business.travel.app.utils;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.business.travel.app.constant.ThreadPoolConstant;

/**
 * @author chenshang
 */
public class FutureUtil {
    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, ThreadPoolConstant.COMMON_THREAD_POOL);
    }

    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
        return CompletableFuture.supplyAsync(supplier, ThreadPoolConstant.COMMON_THREAD_POOL);
    }
}
