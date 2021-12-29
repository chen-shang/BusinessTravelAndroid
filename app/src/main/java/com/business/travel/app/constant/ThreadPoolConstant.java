package com.business.travel.app.constant;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.util.Log;
import com.business.travel.utils.JacksonUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author chenshang
 */
public class ThreadPoolConstant {

	private static final RejectedExecutionHandler DEFAULT_REJECTED_EXECUTION_HANDLER = (r, executor) -> Log.e("ThreadPoolComponent", "init error: " + JacksonUtil.toString(executor));
	/**
	 * 通用线程池
	 */
	public static final ExecutorService COMMON_THREAD_POOL = new ThreadPoolExecutor(20, 25, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("commonThreadPool-%d").build(), DEFAULT_REJECTED_EXECUTION_HANDLER);
}
