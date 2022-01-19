package com.business.travel.app.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chenshang
 * 只要是被 Async 注解的方法都会使用异步线程执行
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Async {
    /**
     * 异步执行,超时时间,单位秒,默认5
     */
    int timeout() default 5;
}
