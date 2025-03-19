package com.evo.middleware.ratelimiter.valve.strategy.token;

import com.evo.middleware.ratelimiter.annotation.EvoRateLimiter;
import com.evo.middleware.ratelimiter.valve.strategy.RateLimiterStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketLimiterStrategy implements RateLimiterStrategy {

    private final Map<String, LimiterConfig> limiterMap = new ConcurrentHashMap<>();

    private static final TokenBucketLimiterStrategy INSTANCE = new TokenBucketLimiterStrategy();

    public static TokenBucketLimiterStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean tryAcquire(EvoRateLimiter rateLimiter, String apiPath) {
        LimiterConfig config = getConfig(apiPath, rateLimiter.permitsPerSecond());
        return doTryAcquire(config, apiPath);
    }

    public synchronized boolean doTryAcquire(LimiterConfig config, String apiPath) {
        Long lastTime = config.lastTime;
        if (lastTime == null) {
            config.lastTime = System.currentTimeMillis();
            this.limiterMap.put(apiPath, config);
            return true;
        }
        refillTokens(config, apiPath);
        if (config.token > 0) {
            config.token--;
            this.limiterMap.put(apiPath, config);
            return true;
        }
        return false;
    }

    /**
     * 补充令牌
     */
    private void refillTokens(LimiterConfig config, String apiPath) {
        long now = System.currentTimeMillis();
        long elapsedTimeMillis = now - config.lastTime;

        // 计算这段时间内可以生成的令牌数量
        long newTokens = (long) (elapsedTimeMillis / 1000.0 * config.permitsPerSecond);
        if (newTokens > 0) {
            config.lastTime += Math.round(newTokens * 1000 / config.permitsPerSecond);
            config.token += newTokens;
        }
    }

    private LimiterConfig getConfig(String apiPath, double permitsPerSecond) {
        return limiterMap.computeIfAbsent(apiPath, config -> new LimiterConfig(permitsPerSecond));
    }

    public static class LimiterConfig {
        private final double permitsPerSecond; // 限流阈值
        private Long lastTime; // 上一次请求的时间
        private Long token;

        public LimiterConfig(double permitsPerSecond) {
            this.permitsPerSecond = permitsPerSecond;
            this.token = 0L;
        }
    }

}
