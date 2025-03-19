package com.evo.middleware.redis.interfaces;

import com.evo.middleware.redis.utils.RedisOperatorClient;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private RedisOperatorClient redisOperatorClient;
    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/set")
    public String set() {
        redisOperatorClient.string().set("test", "test111");
        logger.info("test是否存在:{}", redisOperatorClient.base().hasKey("test"));
        String string = (String) redisOperatorClient.string().get("test");
//        redisOperatorClient.base().delete("test");
        return string;
    }

    @GetMapping("/set/redisson")
    public String redissonSet() {
        RBucket<String> bucket = redissonClient.getBucket("redisson");
        bucket.set("Hello, Redisson!");

        String value = bucket.get();
        System.out.println("Value: " + value);
//        // 删除字符串
//        bucket.delete();
//
//        // 验证是否删除成功
//        boolean exists = bucket.isExists();
//        System.out.println("Exists after deletion: " + exists);
        return value;
    }

    @GetMapping("/lock")
    public String lock() {
        RLock lock = redissonClient.getLock("lockKey");
        try {
            // 尝试加锁，最多等待 10 秒，锁持有时间 30 秒
            boolean isLocked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (isLocked) {
                try {
                    System.out.println("111111");
                    Thread.sleep(20*1000);
                } finally {
                    lock.unlock();
                }
                return "success";
            }else {
                return "not get lock";
            }
        } catch (InterruptedException e) {
            return "get lock error";
        }
    }

    @GetMapping("/queue")
    public String queue() throws InterruptedException {
        RQueue<String> queue = redissonClient.getQueue("queueName");
        queue.add("message");
        Thread.sleep(5000);
        return queue.poll();
    }

    @GetMapping("/map")
    public String map() throws InterruptedException {
        RMap<String,String> map = redissonClient.getMap("map");
        map.put("aaa", "aaaakkkk");
        Thread.sleep(5000);
        return map.get("aaa");
    }
}
