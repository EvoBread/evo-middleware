package com.evo.middleware.ratelimiter.annotation;

import com.evo.middleware.ratelimiter.valve.ValveType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EvoRateLimiter {

    ValveType valveType() default ValveType.TOKEN_BUCKET;

    int subWindowSize() default 2;

    double permitsPerSecond() default 2D;   // 限流许可量

    String returnJson() default "";         // 失败结果 JSON
}
