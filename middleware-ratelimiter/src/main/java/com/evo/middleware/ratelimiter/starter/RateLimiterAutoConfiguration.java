package com.evo.middleware.ratelimiter.starter;

import com.evo.middleware.ratelimiter.aspect.RateLimiterAspect;
import com.evo.middleware.ratelimiter.valve.RateLimiterValve;
import com.evo.middleware.ratelimiter.valve.IValveService;
import com.evo.middleware.ratelimiter.valve.RedisRateLimiterValve;
import com.evo.middleware.redis.utils.RedisOperatorClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterAutoConfiguration {

    @ConditionalOnClass(name = "com.evo.middleware.redis.utils.RedisOperatorClient")
    @ConditionalOnMissingBean(IValveService.class)
    @Configuration
    public static class RedisIValveServiceConfiguration {

        @Bean
        public IValveService valveService(RedisOperatorClient client) {
            return new RedisRateLimiterValve(client);
        }
    }

    @ConditionalOnMissingClass("com.evo.middleware.redis.utils.RedisOperatorClient")
    @ConditionalOnMissingBean(IValveService.class)
    @Configuration
    public static class IValveServiceConfiguration {
        @Bean
        public IValveService valveService() {
            return new RateLimiterValve();
        }
    }

    @Bean
    public RateLimiterAspect rateLimiterAspect(IValveService valveService) {
        return new RateLimiterAspect(valveService);
    }

}
