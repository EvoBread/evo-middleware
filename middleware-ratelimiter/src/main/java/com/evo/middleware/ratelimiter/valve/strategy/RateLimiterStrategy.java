package com.evo.middleware.ratelimiter.valve.strategy;

import com.evo.middleware.ratelimiter.annotation.EvoRateLimiter;

public interface RateLimiterStrategy {
    boolean tryAcquire(EvoRateLimiter rateLimiter, String apiPath);
}
