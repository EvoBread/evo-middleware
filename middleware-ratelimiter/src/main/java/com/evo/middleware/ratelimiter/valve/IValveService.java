package com.evo.middleware.ratelimiter.valve;

import com.evo.middleware.ratelimiter.annotation.EvoRateLimiter;

public interface IValveService {

    boolean tryAcquire(EvoRateLimiter rateLimiter, String apiPath);
}
