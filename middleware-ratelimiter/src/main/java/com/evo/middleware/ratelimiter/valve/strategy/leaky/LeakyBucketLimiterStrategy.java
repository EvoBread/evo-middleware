package com.evo.middleware.ratelimiter.valve.strategy.leaky;

import com.evo.middleware.ratelimiter.annotation.EvoRateLimiter;
import com.evo.middleware.ratelimiter.valve.strategy.RateLimiterStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LeakyBucketLimiterStrategy implements RateLimiterStrategy {

    private final Map<String, LimiterConfig> limiterMap = new ConcurrentHashMap<>();

    private static final LeakyBucketLimiterStrategy INSTANCE = new LeakyBucketLimiterStrategy();

    public static LeakyBucketLimiterStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean tryAcquire(EvoRateLimiter rateLimiter, String apiPath) {
        LimiterConfig config = getConfig(apiPath, rateLimiter.permitsPerSecond());
        return doTryAcquire(config, apiPath);
    }

    public synchronized boolean doTryAcquire(LimiterConfig config, String apiPath) {
        double permitsPerSecond = config.permitsPerSecond;
        Long lastTime = config.lastTime;
        if (lastTime == null) {
            config.lastTime = System.currentTimeMillis();
            this.limiterMap.put(apiPath, config);
            return true;
        }
        long intervalMillis = Math.round(1000 / permitsPerSecond);
        // 计算从上次漏水到现在的时间间隔
        long elapsedTime = System.currentTimeMillis() - lastTime;
        if (elapsedTime > intervalMillis) {
            // 更新时间戳，表示处理了一个请求
            config.lastTime += intervalMillis;  // 下次允许请求的时间点
            this.limiterMap.put(apiPath, config);
            return true;
        } else {
            return false;
        }
    }

    private LimiterConfig getConfig(String apiPath, double permitsPerSecond) {
        return limiterMap.computeIfAbsent(apiPath, config -> new LimiterConfig(permitsPerSecond));
    }

    public static class LimiterConfig {
        private final double permitsPerSecond; // 限流阈值
        private Long lastTime; // 上一次请求的时间

        public LimiterConfig(double permitsPerSecond) {
            this.permitsPerSecond = permitsPerSecond;
        }
    }
}
