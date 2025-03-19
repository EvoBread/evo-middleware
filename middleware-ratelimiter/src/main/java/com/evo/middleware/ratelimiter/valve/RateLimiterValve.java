package com.evo.middleware.ratelimiter.valve;

import com.evo.middleware.ratelimiter.annotation.EvoRateLimiter;
import com.evo.middleware.ratelimiter.valve.strategy.count.CounterLimiterStrategy;
import com.evo.middleware.ratelimiter.valve.strategy.token.GuavaTokenBucketLimiterStrategy;
import com.evo.middleware.ratelimiter.valve.strategy.leaky.LeakyBucketLimiterStrategy;
import com.evo.middleware.ratelimiter.valve.strategy.RateLimiterStrategy;
import com.evo.middleware.ratelimiter.valve.strategy.sliding.SlidingWindowLimiterStrategy;
import com.evo.middleware.ratelimiter.valve.strategy.token.TokenBucketLimiterStrategy;

public class RateLimiterValve implements IValveService{

    @Override
    public boolean tryAcquire(EvoRateLimiter rateLimiter, String apiPath) {
        if (rateLimiter.permitsPerSecond() <= 0) {
            throw new IllegalArgumentException("Rate must be greater than 0");
        }
        return getRateLimiterStrategy(rateLimiter.valveType()).tryAcquire(rateLimiter, apiPath);
    }

    protected RateLimiterStrategy getRateLimiterStrategy(ValveType type) {
        return switch (type) {
            case COUNTER_LIMITER -> CounterLimiterStrategy.getInstance();
            case SLIDING_WINDOW -> SlidingWindowLimiterStrategy.getInstance();
            case LEAKY_BUCKET -> LeakyBucketLimiterStrategy.getInstance();
            case GUAVA_TOKEN_BUCKET -> GuavaTokenBucketLimiterStrategy.getInstance();
            case TOKEN_BUCKET -> TokenBucketLimiterStrategy.getInstance();
            default -> throw new IllegalArgumentException("RateLimiter valveType is not supported");
        };
    }
}
