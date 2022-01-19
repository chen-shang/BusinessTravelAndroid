package com.business.travel.app.api;

import androidx.annotation.Nullable;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.business.travel.app.constant.AppConfig;
import com.business.travel.app.model.Config;
import com.business.travel.app.model.GiteeContent;
import com.business.travel.app.utils.HttpWrapper;
import com.business.travel.app.utils.NetworkUtil;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.JacksonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.*;
import okhttp3.Request.Builder;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.util.*;

/**
 * @author chenshang
 *
 * 远程资源API
 */
public class BusinessTravelResourceApi {

    private static final HttpWrapper httpClient = HttpWrapper.withOkHttpClient(new OkHttpClient());
    private static final String ACCESS_TOKEN = "a1b50339ccf80a7c96f6a96fa97fcdaf";

    /**
     * 获取图标
     *
     * @param iconFullName
     * @return
     */
    @Nullable
    public static InputStream getIcon(String iconFullName) {
        if (StringUtils.isBlank(iconFullName)) {
            return null;
        }

        try {
            //先从缓存中获取
            InputStream inputStream = getIconFromCache(iconFullName);
            if (inputStream != null) {
                LogUtils.d("命中图片缓存:" + iconFullName);
                return inputStream;
            }

            LogUtils.d("图片未命中缓存:" + iconFullName);
            //未命中缓存再从服务端获取
            inputStream = getIconFromServer(iconFullName);
            if (inputStream == null) {
                LogUtils.d("未查询到图片:" + iconFullName);
                return null;
            }
            //获取后加到缓存中
            return addIconToCache(iconFullName, inputStream);
        } catch (Exception e) {
            LogUtils.e("获取图标文件失败,请稍后重试:" + e.getMessage());
        }
        return null;
    }

    /***
     * 将图标加入到本地缓存中
     * @param iconFullName
     * @param inputStream
     * @return
     * @throws FileNotFoundException
     */
    private static InputStream addIconToCache(String iconFullName, InputStream inputStream) throws FileNotFoundException {
        //参数检查
        if (inputStream == null) {
            return null;
        }
        //业务逻辑
        String md5 = DigestUtils.md5DigestAsHex(iconFullName.getBytes());
        File file = new File(PathUtils.getExternalAppFilesPath(), md5 + ".svg");
        FileIOUtils.writeFileFromIS(file, inputStream, false);
        //结果处理
        return new FileInputStream(file);
    }

    /**
     * 先从缓存中获取
     *
     * @param iconFullName
     * @return
     * @throws FileNotFoundException
     */
    private static InputStream getIconFromCache(String iconFullName) throws FileNotFoundException {
        String md5 = DigestUtils.md5DigestAsHex(iconFullName.getBytes());
        File file = new File(PathUtils.getExternalAppFilesPath(), md5 + ".svg");
        if (!file.exists()) {
            return null;
        }

        //超过Config::getIconTtl 且网络良好 才删除缓存，否则可能因为网络不好把缓存删除了导致图标加载失败
        long nowTimestamp = DateTimeUtil.timestamp();
        long lastModified = file.lastModified();
        Long iconTtl = Optional.ofNullable(AppConfig.getConfig()).map(Config::getIconTtl).orElse(24 * 60 * 60 * 1000L);
        if (nowTimestamp - lastModified > iconTtl && NetworkUtil.isAvailable()) {
            boolean delete = file.delete();
            LogUtils.i("缓存超时失效" + iconFullName + " :" + delete);
            return null;
        }

        return new FileInputStream(file);
    }

    /**
     * 从远程服务器获取图标流文件
     *
     * @param iconFullName
     * @return
     */
    private static InputStream getIconFromServer(String iconFullName) {
        try {
            if (!NetworkUtil.isAvailable()) {
                return null;
            }

            return getResponseBody(iconFullName).byteStream();
        } catch (IOException e) {
            throw new IllegalArgumentException("获取图标失败", e);
        }
    }

    /**
     * 获取仓库具体路径下的内容
     * https://gitee.com/api/v5/swagger#/getV5ReposOwnerRepoContents(Path)
     */
    public static List<GiteeContent> getRepoContents(String path) {
        String uri = "https://gitee.com/api/v5/repos/chen-shang/business-travel-resource/contents/" + path;
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(uri)).newBuilder().addQueryParameter("access_token", ACCESS_TOKEN);
        Request request = new Builder().url(urlBuilder.build()).build();
        try {
            String response = httpClient.sendRequest(request);
            LogUtils.i(new StringJoiner("\n").add("获取仓库具体路径下的内容").add("uri:" + uri).add("response:" + response).toString());
            if (StringUtils.isEmpty(response)) {
                return Collections.emptyList();
            }
            return JacksonUtil.toBean(response, new TypeReference<List<GiteeContent>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("网络异常,请稍后重试", e);
        }
    }

    /**
     * 获取远程文件内容
     *
     * @param path
     * @return
     */
    public static String getRepoRaw(String path) {
        try {
            String uri = "https://gitee.com/chen-shang/business-travel-resource/raw/" + path;
            return getResponseBody(uri).string();
        } catch (IOException e) {
            throw new IllegalArgumentException("获取文件失败", e);
        }
    }

    @NotNull
    private static ResponseBody getResponseBody(String uri) throws IOException {
        Request request = new Builder().url(uri).build();
        Response response = new OkHttpClient().newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new RuntimeException("请求失败:" + response.code() + ",请稍后再试");
        }
        ResponseBody body = response.body();
        if (body == null) {
            throw new RuntimeException("请求失败,请稍后再试");
        }
        return body;
    }
}
