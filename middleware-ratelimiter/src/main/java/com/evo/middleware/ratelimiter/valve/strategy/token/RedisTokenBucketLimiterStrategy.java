package com.evo.middleware.ratelimiter.valve.strategy.token;

import com.evo.middleware.ratelimiter.annotation.EvoRateLimiter;
import com.evo.middleware.ratelimiter.valve.strategy.RateLimiterStrategy;
import com.evo.middleware.redis.utils.RedisOperatorClient;
import com.google.common.collect.Lists;
import org.springframework.data.redis.core.script.DefaultRedisScript;


/*
    在 Redis 中实现一个无需容量（即令牌桶中没有固定的令牌存储）的限流算法，并且当超过限流时直接拒绝请求，可以通过一种基于时间戳和计数器的滑动窗口方法来实现。这种方法结合了 令牌桶 的思想和 滑动窗口 的灵活性
    令牌桶算法的核心思想是：在固定的时间窗口内，以固定的速率向桶中放入令牌，当请求到来时，如果桶中存在令牌，则允许请求，否则拒绝请求。

    无需容量的设计 ：
        不需要显式存储令牌的数量，而是通过时间戳和请求记录动态计算令牌的状态。
        使用 Redis 的 Sorted Set 数据结构存储请求的时间戳，利用时间窗口内的请求数模拟令牌的消耗。

    限流逻辑 ：
        每次请求到达时，移除超出时间窗口范围的旧请求。
        判断当前窗口内的请求数是否超过限制。
        如果超过限制，直接拒绝请求；否则允许请求。

    Redis 的优势 ：
        Sorted Set 支持高效的时间戳排序和范围删除操作。
        原子性操作确保分布式环境下的线程安全。
 */
public class RedisTokenBucketLimiterStrategy implements RateLimiterStrategy {

    private static final RedisTokenBucketLimiterStrategy INSTANCE = new RedisTokenBucketLimiterStrategy();
    private static final String CACHE_KEY = "token_bucket_limiter:";
    private RedisOperatorClient redisOperatorClient;


    public static RedisTokenBucketLimiterStrategy getInstance(RedisOperatorClient redisOperatorClient) {
        if (redisOperatorClient == null) throw new IllegalArgumentException("middleware-redis Not dependent");
        INSTANCE.redisOperatorClient = redisOperatorClient;
        return INSTANCE;
    }

    @Override
    public boolean tryAcquire(EvoRateLimiter rateLimiter, String apiPath) {
        String key = CACHE_KEY + apiPath;
        long currentTime = System.currentTimeMillis();
        long windowSize = 1000; // 1秒窗口
        double permitsPerSecond = rateLimiter.permitsPerSecond();

//        // 1. 清理窗口外的旧请求
//        redisOperatorClient.zset().zRemoveRangeByScore(key, 0, currentTime - windowSize);
//
//        // 2. 获取当前窗口内的请求数
//        long requestCount = redisOperatorClient.zset().zCard(key);
//
//        if (requestCount < permitsPerSecond) {
//            // 3. 允许请求，将当前请求时间戳加入 Sorted Set
//            redisOperatorClient.zset().zAdd(key, currentTime, (double) currentTime);
//            return true; // 允许请求
//        } else {
//            return false; // 拒绝请求
//        }

        String luaScript =
                """
                        local currentTime = ARGV[1]
                        local windowSize = ARGV[2]
                        local permitsPerSecond = tonumber(ARGV[3])
                        local key = KEYS[1]

                        -- 1. 清理窗口外的旧请求
                         redis.call('ZREMRANGEBYSCORE', key, 0, currentTime - windowSize)

                        -- 2. 获取当前窗口内的请求数
                         local requestCount = redis.call('ZCARD', key)

                        -- 3. 判断是否允许请求
                         if requestCount < permitsPerSecond then
                            -- 4. 允许请求，将当前请求时间戳加入 Sorted Set
                            redis.call('ZADD', key, currentTime, currentTime)
                            -- 设置键的过期时间（避免残留）
                            redis.call('PEXPIRE', key, windowSize * 2)
                            return true
                        else
                            return false
                        end
                        """;

        Boolean result = redisOperatorClient.redisTemplate().execute(new DefaultRedisScript<>(luaScript, Boolean.class),
                Lists.newArrayList(key), currentTime, windowSize, permitsPerSecond);
        return Boolean.TRUE.equals(result);
    }
}
