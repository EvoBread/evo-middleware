package com.evo.middleware.ratelimiter.valve.strategy.token;

import com.evo.middleware.ratelimiter.annotation.EvoRateLimiter;
import com.evo.middleware.ratelimiter.valve.strategy.RateLimiterStrategy;
import com.evo.middleware.redis.utils.RedisOperatorClient;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;

public class RedissonTokenBucketLimiterStrategy implements RateLimiterStrategy {

    private static final RedissonTokenBucketLimiterStrategy INSTANCE = new RedissonTokenBucketLimiterStrategy();
    private static final String CACHE_KEY = "token_bucket_limiter:";
    private RedisOperatorClient redisOperatorClient;


    public static RedissonTokenBucketLimiterStrategy getInstance(RedisOperatorClient redisOperatorClient) {
        if (redisOperatorClient == null) throw new IllegalArgumentException("middleware-redis Not dependent");
        INSTANCE.redisOperatorClient = redisOperatorClient;
        return INSTANCE;
    }

    @Override
    public boolean tryAcquire(EvoRateLimiter rateLimiter, String apiPath) {
        RRateLimiter limiter = redisOperatorClient.redissonClient().getRateLimiter(CACHE_KEY + apiPath);
        limiter.trySetRate(RateType.OVERALL, ((Double) rateLimiter.permitsPerSecond()).longValue(), 1, RateIntervalUnit.SECONDS);
        return limiter.tryAcquire();
    }
}
