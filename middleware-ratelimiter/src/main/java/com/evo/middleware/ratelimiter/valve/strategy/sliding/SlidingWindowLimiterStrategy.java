package com.evo.middleware.ratelimiter.valve.strategy.sliding;

import com.evo.middleware.ratelimiter.annotation.EvoRateLimiter;
import com.evo.middleware.ratelimiter.valve.strategy.RateLimiterStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SlidingWindowLimiterStrategy implements RateLimiterStrategy {

    private final Map<String, LimiterConfig> limiterMap = new ConcurrentHashMap<>();

    private ScheduledExecutorService scheduler; // 定时任务调度器

    private static final SlidingWindowLimiterStrategy INSTANCE = new SlidingWindowLimiterStrategy(Executors.newSingleThreadScheduledExecutor());

    private SlidingWindowLimiterStrategy(){}

    private SlidingWindowLimiterStrategy(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
        startCleanupTask();
    }

    public static SlidingWindowLimiterStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean tryAcquire(EvoRateLimiter rateLimiter, String apiPath) {
        LimiterConfig config = getConfig(apiPath, rateLimiter.permitsPerSecond(), rateLimiter.subWindowSize());
        return doTryAcquire(config, apiPath);
    }

    public synchronized boolean doTryAcquire(LimiterConfig config, String apiPath) {
        double permitsPerSecond = config.permitsPerSecond;
        int subWindowCount = config.subWindowSize;
        Map<Long, Integer> subWindows = config.subWindows;

        long now = System.currentTimeMillis();

//        // 懒清理, 每次加载的时候清理过期的子窗口
//        long windowStart = now - windowSize; // 当前时间窗口的起始时间
//        // 清理过期的子窗口
//        subWindows.entrySet().removeIf(entry -> entry.getKey() < windowStart);

        int totalCount = subWindows.values().stream().mapToInt(Integer::intValue).sum();
        if (totalCount >= permitsPerSecond) {
            return false;
        }

        /*
            当前子窗口
                (windowSize / subWindowCount)：这部分计算了每个子窗口的大小
                now / (windowSize / subWindowCount)：这部分计算了当前时间 now 属于第几个子窗口
                * (windowSize / subWindowCount)：这部分将子窗口的索引转换回时间戳，表示当前子窗口的起始时间
         */
        long currentSubWindow = (now / (1000 / subWindowCount)) * (1000 / subWindowCount);
        subWindows.put(currentSubWindow, subWindows.getOrDefault(currentSubWindow, 0) + 1);
        this.limiterMap.put(apiPath, config);
        return true;
    }

    private void startCleanupTask() {
        /*
              参数说明：
                第一个参数：初始延迟（1 秒）。
                第二个参数：执行间隔（1 秒）。
                第三个参数：时间单位（秒）。
         */
        scheduler.scheduleAtFixedRate(() -> {
            this.limiterMap.forEach((apiPath, config) -> {
                long now = System.currentTimeMillis();
                long windowStart = now - 1000; // 当前时间窗口的起始时间
                // 清理过期的子窗口
                config.subWindows.entrySet().removeIf(entry -> entry.getKey() < windowStart);
            });
        }, 1, 1, TimeUnit.SECONDS); // 每秒执行一次清理任务
    }

    private LimiterConfig getConfig(String apiPath, double permitsPerSecond, int subWindowSize){
        return limiterMap.computeIfAbsent(apiPath, config -> new LimiterConfig(permitsPerSecond, subWindowSize));
    }

    public static class LimiterConfig {
        private final double permitsPerSecond; // 限流阈值
        private final int subWindowSize;
        private final Map<Long, Integer> subWindows;

        public LimiterConfig(double permitsPerSecond, int subWindowSize) {
            this.permitsPerSecond = permitsPerSecond;
            this.subWindowSize = subWindowSize;
            this.subWindows = new ConcurrentHashMap<>();
        }
    }

}
