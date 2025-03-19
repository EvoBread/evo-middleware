package com.evo.middleware.ratelimiter.valve.strategy.count;

import com.evo.middleware.ratelimiter.annotation.EvoRateLimiter;
import com.evo.middleware.ratelimiter.valve.strategy.RateLimiterStrategy;
import com.evo.middleware.redis.utils.RedisOperatorClient;
import com.google.common.collect.Lists;
import org.springframework.data.redis.core.script.DefaultRedisScript;

public class RedisCounterLimiterStrategy implements RateLimiterStrategy {

    private static final RedisCounterLimiterStrategy INSTANCE = new RedisCounterLimiterStrategy();
    private static final String CACHE_KEY = "counter_limiter:";
    private RedisOperatorClient redisOperatorClient;

    public static RedisCounterLimiterStrategy getInstance(RedisOperatorClient redisOperatorClient) {
        if (redisOperatorClient == null) throw new IllegalArgumentException("middleware-redis Not dependent");
        INSTANCE.redisOperatorClient = redisOperatorClient;
        return INSTANCE;
    }

    @Override
    public boolean tryAcquire(EvoRateLimiter rateLimiter, String apiPath) {
        String key = CACHE_KEY + apiPath;
//        Object countStr = redisOperatorClient.string().get(key);
//        if (countStr == null) {
//            // 初始化计数器并设置过期时间
//            redisOperatorClient.string().setEx(key, 1, 1, TimeUnit.SECONDS);
//            return true;
//        } else {
//            int count = (int) countStr;
//            if (count < rateLimiter.permitsPerSecond()) {
//                // 未达到限制，增加计数
//                redisOperatorClient.string().incrBy(key, 1);
//                return true;
//            } else {
//                return false;
//            }
//        }

        String luaScript = """
                local count = redis.call('GET', KEYS[1])
                if count == false then
                    redis.call('SET', KEYS[1], 1, 'EX', 1)
                    return true
                else
                    count = tonumber(count)
                    if count < tonumber(ARGV[1]) then
                        redis.call('INCRBY', KEYS[1], 1)
                        return true
                    else
                        return false
                    end
                end
                """;
        Boolean result = redisOperatorClient.redisTemplate().execute(new DefaultRedisScript<>(luaScript, Boolean.class), Lists.newArrayList(key), rateLimiter.permitsPerSecond());
        return Boolean.TRUE.equals(result);
    }
}
