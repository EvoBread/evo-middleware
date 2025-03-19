package com.evo.middleware.ratelimiter.valve.strategy.count;

import com.evo.middleware.ratelimiter.annotation.EvoRateLimiter;
import com.evo.middleware.ratelimiter.valve.strategy.RateLimiterStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CounterLimiterStrategy implements RateLimiterStrategy {

    private final Map<String, LimiterConfig> limiterMap = new ConcurrentHashMap<>();

    private static final CounterLimiterStrategy INSTANCE = new CounterLimiterStrategy();

    public static CounterLimiterStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean tryAcquire(EvoRateLimiter rateLimiter, String apiPath) {
        LimiterConfig config = getConfig(apiPath, rateLimiter.permitsPerSecond());
        return doTryAcquire(config, apiPath);
    }

    public synchronized boolean doTryAcquire(LimiterConfig config, String apiPath) {
        double permitsPerSecond = config.permitsPerSecond;
        long lastTime = config.lastTime;
        int counter = config.counter;

        long now = System.currentTimeMillis();
        // 超出时间窗口重新计数
        if (now - lastTime > 1000) {
            config.counter = 0;  // 重置计数器
            config.lastTime = now; // 重置时间
        }
        // 时间窗口内未超过阈值
        if (counter < permitsPerSecond) {
            config.counter++;
            this.limiterMap.put(apiPath, config);
            return true;
        }
        return false;
    }

    private LimiterConfig getConfig(String apiPath, double permitsPerSecond){
        return limiterMap.computeIfAbsent(apiPath, config -> new LimiterConfig(permitsPerSecond));
    }

    public static class LimiterConfig {
        private final double permitsPerSecond; // 限流阈值
        private long lastTime; // 上一次请求的时间
        private int counter; // 计数器

        public LimiterConfig(double permitsPerSecond) {
            this.permitsPerSecond = permitsPerSecond;
            this.lastTime = System.currentTimeMillis();
            this.counter = 0;
        }
    }
}
