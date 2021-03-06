package com.qyf.jwt;

import com.alibaba.fastjson.JSON;
import com.qyf.jwt.cache.TokenCache;
import com.qyf.jwt.cache.UserCache;
import com.qyf.jwt.entity.Token;
import com.qyf.jwt.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@Slf4j
class JwtApplicationTests {

    @Autowired
    TokenCache tokenCache;

    @Autowired
    UserCache userCache;

    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    void contextLoads() {
    }

    @Test
    void pushList() {
        String id = UUID.randomUUID().toString();
        Token token = Token.builder().id(id)
                .userId("1").token(id + "_1").expTime(new Date()).ip("127.0.0.1").build();
        tokenCache.pushList("list", token);
        token = tokenCache.popList("list");
        log.info("token:{}", token);
    }

//	@Test
//	void popList(){
//		Token token = redisCache.popList("list");
//		log.info("token:{}", token);
//	}

    @Test
    void loadData() {
        User user = User.builder().id(UUID.randomUUID().toString())
                .username("qyf").password("123").build();
        userCache.pushList("user", user);
        List<User> users = userCache.findAll("user");
        log.info("users:{}", users);
    }


    @Test
    void zadd() {
        userCache.zadd("users", "1", 1L);
        userCache.zadd("users", "2", 2L);
        userCache.zadd("users", "3", 3L);
        userCache.zadd("users", "4", 1L);
        userCache.zadd("users", "5", 0L);
        Set<ZSetOperations.TypedTuple> key = userCache.getScore("users");
        log.info("users:{}", JSON.toJSONString(key));
    }

    //????????????
    @Test
    void setBit() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String time = format.format(date);
        for (int i = 0; i < 7; i++) {
            if (i == 3) {
                continue;
            } else {
                userCache.addBit(time, Long.valueOf(i), true);
            }
        }
        Long count = userCache.bitCount(time);
        log.info("????????????:{}???", count);
    }

    //??????????????????
    @Test
    void clearBit() {
        String str = (String) redisTemplate.opsForList().rightPop("user_bit_1");
        redisTemplate.delete(str);
    }

    //????????????????????????
    @Test
    void check() {
        Long count = (Long) redisTemplate.execute((RedisCallback<Long>) con -> con.bitOp(RedisStringCommands.BitOperation.AND,
                "test1".getBytes(), "login:20210325".getBytes(), "login:20210326".getBytes()));
        log.info("count:{}", count);
        Boolean test1 = userCache.getBit("test1", 3L);
//		System.out.println(redisTemplate.expire("test1", 5, TimeUnit.SECONDS));
        System.out.println(redisTemplate.getExpire("test1"));
        log.info("state:{}", test1);
    }

    @Test
    void expire() {
        redisTemplate.opsForValue().set("test2", "1");
        System.out.println(redisTemplate.expire("test2", 5, TimeUnit.SECONDS));
        System.out.println(redisTemplate.getExpire("test2"));
    }

    public static void main(String[] args) throws Exception {
        Semaphore semaphore = new Semaphore(1);
        AtomicInteger count = new AtomicInteger(0);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    count.incrementAndGet();
                    System.out.println("????????????");
                    System.out.println(count.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();
                }
            }).start();
        }
    }

}
