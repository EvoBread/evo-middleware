package com.evo.middleware.ratelimiter.valve;

public enum ValveType {
    COUNTER_LIMITER,
    REDIS_COUNTER_LIMITER,

    SLIDING_WINDOW,

    LEAKY_BUCKET,

    TOKEN_BUCKET,
    GUAVA_TOKEN_BUCKET,
    REDIS_TOKEN_BUCKET,
    REDISSION_TOKEN_BUCKET;

}
