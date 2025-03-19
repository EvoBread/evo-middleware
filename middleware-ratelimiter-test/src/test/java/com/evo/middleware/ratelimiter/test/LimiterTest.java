package com.evo.middleware.ratelimiter.test;

import com.evo.middleware.ratelimiter.annotation.EvoRateLimiter;
import com.evo.middleware.ratelimiter.valve.ValveType;
import com.evo.middleware.ratelimiter.valve.strategy.count.RedisCounterLimiterStrategy;
import com.evo.middleware.ratelimiter.valve.strategy.leaky.LeakyBucketLimiterStrategy;
import com.evo.middleware.ratelimiter.valve.strategy.token.RedisTokenBucketLimiterStrategy;
import com.evo.middleware.ratelimiter.valve.strategy.token.RedissonTokenBucketLimiterStrategy;
import com.evo.middleware.ratelimiter.valve.strategy.token.TokenBucketLimiterStrategy;
import com.evo.middleware.redis.utils.RedisOperatorClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.annotation.Annotation;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LimiterTest {

    @Autowired
    private RedisOperatorClient redisOperatorClient;

    @Test
    public void leakyLimit() throws InterruptedException {
        LeakyBucketLimiterStrategy instance = LeakyBucketLimiterStrategy.getInstance();
        EvoRateLimiter evoRateLimiter = new EvoRateLimiter() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public ValveType valveType() {
                return null;
            }

            @Override
            public int subWindowSize() {
                return 0;
            }

            @Override
            public double permitsPerSecond() {
                return 1;
            }

            @Override
            public String returnJson() {
                return "";
            }
        };

        for (int i = 0; i < 10; i++) {
            System.out.println(instance.tryAcquire(evoRateLimiter, "aaa"));
            Thread.sleep(500L);
        }
    }

    @Test
    public void tokenLimit() throws InterruptedException {
        TokenBucketLimiterStrategy instance = TokenBucketLimiterStrategy.getInstance();
        EvoRateLimiter evoRateLimiter = new EvoRateLimiter() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public ValveType valveType() {
                return null;
            }

            @Override
            public int subWindowSize() {
                return 0;
            }

            @Override
            public double permitsPerSecond() {
                return 1;
            }

            @Override
            public String returnJson() {
                return "";
            }
        };

        for (int i = 0; i < 10; i++) {
            System.out.println(instance.tryAcquire(evoRateLimiter, "aaa"));
            Thread.sleep(500L);
        }
    }

    @Test
    public void redisCountLimit() throws InterruptedException {
        RedisCounterLimiterStrategy instance = RedisCounterLimiterStrategy.getInstance(redisOperatorClient);
        EvoRateLimiter evoRateLimiter = new EvoRateLimiter() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public ValveType valveType() {
                return null;
            }

            @Override
            public int subWindowSize() {
                return 0;
            }

            @Override
            public double permitsPerSecond() {
                return 1;
            }

            @Override
            public String returnJson() {
                return "";
            }
        };

        for (int i = 0; i < 10; i++) {
            System.out.println(instance.tryAcquire(evoRateLimiter, "aaa"));
            Thread.sleep(500L);
        }
    }

    @Test
    public void redisSlidingWindowLimit() throws InterruptedException {
        RedisTokenBucketLimiterStrategy instance = RedisTokenBucketLimiterStrategy.getInstance(redisOperatorClient);
        EvoRateLimiter evoRateLimiter = new EvoRateLimiter() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public ValveType valveType() {
                return null;
            }

            @Override
            public int subWindowSize() {
                return 0;
            }

            @Override
            public double permitsPerSecond() {
                return 2;
            }

            @Override
            public String returnJson() {
                return "";
            }
        };

        for (int i = 0; i < 10; i++) {
            System.out.println(instance.tryAcquire(evoRateLimiter, "aaa"));
            Thread.sleep(200L);
        }
    }

    @Test
    public void redisTokenLimit() throws InterruptedException {
        RedisTokenBucketLimiterStrategy instance = RedisTokenBucketLimiterStrategy.getInstance(redisOperatorClient);
        EvoRateLimiter evoRateLimiter = new EvoRateLimiter() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public ValveType valveType() {
                return null;
            }

            @Override
            public int subWindowSize() {
                return 0;
            }

            @Override
            public double permitsPerSecond() {
                return 1;
            }

            @Override
            public String returnJson() {
                return "";
            }
        };

        for (int i = 0; i < 10; i++) {
            System.out.println(instance.tryAcquire(evoRateLimiter, "aaa"));
            Thread.sleep(500L);
        }
    }

    @Test
    public void redissonTokenLimit() throws InterruptedException {
        RedissonTokenBucketLimiterStrategy instance = RedissonTokenBucketLimiterStrategy.getInstance(redisOperatorClient);
        EvoRateLimiter evoRateLimiter = new EvoRateLimiter() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public ValveType valveType() {
                return null;
            }

            @Override
            public int subWindowSize() {
                return 0;
            }

            @Override
            public double permitsPerSecond() {
                return 1;
            }

            @Override
            public String returnJson() {
                return "";
            }
        };

        for (int i = 0; i < 10; i++) {
            System.out.println(instance.tryAcquire(evoRateLimiter, "aaa"));
            Thread.sleep(500L);
        }
    }
}
