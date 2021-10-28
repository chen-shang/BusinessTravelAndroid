package com.business.travel.app.aop;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import com.blankj.utilcode.util.LogUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import static com.business.travel.app.constant.ThreadPoolConstant.COMMON_THREAD_POOL;

/**
 * @author chenshang
 */
@Aspect
public class AsyncAspect {
	@Pointcut("execution(@com.business.travel.app.aop.Async * *(..))")
	public void async() {
	}

	/**
	 * 只要是被 Async 注解的方法都会使用异步线程执行
	 *
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
	@RequiresApi(api = VERSION_CODES.N)
	@Around("async()")
	public Object process(final ProceedingJoinPoint joinPoint) throws Throwable {
		Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
		Async asyncAnnotation = method.getAnnotation(Async.class);
		Integer timeoutSecond = Optional.ofNullable(asyncAnnotation).map(Async::timeout).orElse(5);
		return CompletableFuture.supplyAsync(() -> {
			try {
				return joinPoint.proceed();
			} catch (Throwable e) {
				LogUtils.i("任务执行异常", e);
				throw new RuntimeException("任务执行异常", e);
			}
		}, COMMON_THREAD_POOL).get(timeoutSecond, TimeUnit.SECONDS);
	}
}
