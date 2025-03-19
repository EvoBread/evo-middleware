package com.evo.middleware.redis.config;

import com.evo.middleware.redis.properties.RedisClientConfigProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置链接
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置自定义序列化方式
        setSerializeConfig(redisTemplate, redisConnectionFactory);
        return redisTemplate;
    }

    private void setSerializeConfig(RedisTemplate<String, Object> redisTemplate, RedisConnectionFactory redisConnectionFactory) {
        // 普通key 和 hashKey 采用StringRedisSerializer进行序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // 解决查询缓存转换异常的问题
        ObjectMapper objectMapper = new ObjectMapper();
        GenericJackson2JsonRedisSerializer redisValueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        // 启用模块支持
        objectMapper.findAndRegisterModules();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 非 final
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        // 在反序列化时是否对未知属性 抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 普通value与hash类型的value采用jackson方式进行序列化
        redisTemplate.setValueSerializer(redisValueSerializer);
        redisTemplate.setHashValueSerializer(redisValueSerializer);
        redisTemplate.afterPropertiesSet();
    }

    @Configuration
    @EnableConfigurationProperties(RedisClientConfigProperties.class)
    public static class RedissonConfig {

        @Bean("redissonClient")
        @ConditionalOnMissingBean
        public RedissonClient redissonClient(ConfigurableApplicationContext applicationContext, RedisClientConfigProperties properties) {
            Config config = new Config();
            // 根据需要可以设定编解码器；https://github.com/redisson/redisson/wiki/4.-%E6%95%B0%E6%8D%AE%E5%BA%8F%E5%88%97%E5%8C%96
            config.setCodec(new JsonJacksonCodec());

            config.useSingleServer()
                    .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
//                .setPassword(properties.getPassword())
                    .setConnectionPoolSize(properties.getPoolSize())
                    .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                    .setIdleConnectionTimeout(properties.getIdleTimeout())
                    .setConnectTimeout(properties.getConnectTimeout())
                    .setRetryAttempts(properties.getRetryAttempts())
                    .setRetryInterval(properties.getRetryInterval())
                    .setPingConnectionInterval(properties.getPingInterval())
                    .setKeepAlive(properties.isKeepAlive())
            ;

            return Redisson.create(config);
        }
    }
}
