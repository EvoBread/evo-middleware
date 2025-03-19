package com.evo.middleware.ratelimiter.test.interfaces;

import com.evo.middleware.ratelimiter.annotation.EvoRateLimiter;
import com.evo.middleware.ratelimiter.valve.ValveType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/count/limit")
    @EvoRateLimiter(valveType = ValveType.COUNTER_LIMITER, returnJson = "{\"code\":10000, \"msg\":\"已被限流\"}")
    public String countLimit() {
        return "rate_limiter_hello";
    }

    @GetMapping("/sliding/window")
    @EvoRateLimiter(valveType = ValveType.SLIDING_WINDOW, returnJson = "{\"code\":10000, \"msg\":\"已被限流\"}")
    public String slidingWindow() {
        return "sliding_window_hello";
    }

    @GetMapping("/leaky/bucket")
    @EvoRateLimiter(valveType = ValveType.LEAKY_BUCKET, permitsPerSecond = 1D,returnJson = "{\"code\":10000, \"msg\":\"已被限流\"}")
    public String leakyBucket() {
        return "leaky_bucket_hello";
    }

}
