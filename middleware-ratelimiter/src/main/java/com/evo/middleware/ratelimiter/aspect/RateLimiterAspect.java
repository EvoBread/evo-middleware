package com.evo.middleware.ratelimiter.aspect;

import com.alibaba.fastjson2.JSON;
import com.evo.middleware.ratelimiter.annotation.EvoRateLimiter;
import com.evo.middleware.ratelimiter.valve.IValveService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

@Aspect
public class RateLimiterAspect {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterAspect.class);

    private final IValveService valveService;

    public RateLimiterAspect(IValveService valveService) {
        this.valveService = valveService;
        logger.debug("RateLimiterAspect init success");
    }

    @Pointcut("@annotation(com.evo.middleware.ratelimiter.annotation.EvoRateLimiter)")
    public void pointCut() {
    }

    @Around("pointCut() && @annotation(evoRateLimiter)")
    public Object around(ProceedingJoinPoint jp, EvoRateLimiter evoRateLimiter) throws Throwable {
        // 1. 获取方法签名
        Method method = getMethod(jp);
        String className = jp.getTarget().getClass().getName();
        String methodName = method.getName();

        // 2. 拼接唯一标识符（类似路由）
        String apiPath = className + "#" + methodName;

        if (!valveService.tryAcquire(evoRateLimiter, apiPath)) {
            return JSON.parseObject(evoRateLimiter.returnJson(), method.getReturnType());
        }
        return jp.proceed();
    }

    private Method getMethod(JoinPoint jp) throws NoSuchMethodException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        return jp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }
}
