package com.business.travel.app.utils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 网络剧是否通畅
 */
public class NetworkUtil {
    private static final int TIMEOUT = 5;
    /**
     * 缓存一下网络状态
     * 这里倾向于认为如果某次网络是好的的,未来1分钟内网络肯定是好的,如果网络是不好的,未来一分钟内不好的概率也比较大,这里可以减少发起http请求
     */
    private static final LoadingCache<String, Boolean> CACHE = CacheBuilder.newBuilder().maximumSize(1).expireAfterWrite(3, TimeUnit.MINUTES).build(new CacheLoader<String, Boolean>() {
        @Override
        public Boolean load(@NotNull String key) {
            //默认3秒获取不到就返回false
            return isAvailable(3);
        }
    });

    /**
     * 当前网络是否通畅
     */
    public static boolean isAvailable() {
        try {
            return CACHE.get("network");
        } catch (ExecutionException e) {
            LogUtils.e("测试网络是否畅通异常,从缓存获取失败", e);
        }
        return isAvailable(TIMEOUT);
    }

    /**
     * 当前网络是否通畅
     *
     * @param timeout 单位秒
     * @return
     */
    private static boolean isAvailable(long timeout) {
        try {
            return FutureUtil.supplyAsync(NetworkUtils::isAvailable).get(timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            LogUtils.e("测试网络是否畅通异常", e);
        }
        return false;
    }
}
