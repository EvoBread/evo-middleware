package com.evo.middleware.ratelimiter.valve;

import com.evo.middleware.ratelimiter.valve.strategy.RateLimiterStrategy;
import com.evo.middleware.ratelimiter.valve.strategy.count.RedisCounterLimiterStrategy;
import com.evo.middleware.ratelimiter.valve.strategy.token.RedisTokenBucketLimiterStrategy;
import com.evo.middleware.ratelimiter.valve.strategy.token.RedissonTokenBucketLimiterStrategy;
import com.evo.middleware.redis.utils.RedisOperatorClient;

public class RedisRateLimiterValve extends RateLimiterValve{

    private final RedisOperatorClient redisOperatorClient;

    public RedisRateLimiterValve(RedisOperatorClient redisOperatorClient) {
        this.redisOperatorClient = redisOperatorClient;
    }

    protected RateLimiterStrategy getRateLimiterStrategy(ValveType type) {
        return switch (type) {
            case REDIS_COUNTER_LIMITER -> RedisCounterLimiterStrategy.getInstance(redisOperatorClient);
            case REDIS_TOKEN_BUCKET -> RedisTokenBucketLimiterStrategy.getInstance(redisOperatorClient);
            case REDISSION_TOKEN_BUCKET -> RedissonTokenBucketLimiterStrategy.getInstance(redisOperatorClient);
            default -> super.getRateLimiterStrategy(type);
        };
    }
}
