package com.evo.middleware.ratelimiter.valve.strategy.token;

import com.evo.middleware.ratelimiter.annotation.EvoRateLimiter;
import com.evo.middleware.ratelimiter.valve.strategy.RateLimiterStrategy;
import com.google.common.util.concurrent.RateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GuavaTokenBucketLimiterStrategy implements RateLimiterStrategy {

    private final static Map<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    private static final GuavaTokenBucketLimiterStrategy INSTANCE = new GuavaTokenBucketLimiterStrategy();

    public static GuavaTokenBucketLimiterStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean tryAcquire(EvoRateLimiter rateLimiter, String apiPath) {
        return rateLimiterMap.computeIfAbsent(
                apiPath,
                key -> RateLimiter.create(rateLimiter.permitsPerSecond())
        ).tryAcquire();
    }
}
