package com.qyf.jwt.cache;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.lang.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class RedisCache<T> {

    @Autowired
    protected RedisTemplate redisTemplate;

    protected Class<T> clz;

    public RedisCache() {
        clz = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void pushList(String key, Object value){
        String jsonObj = JSON.toJSONString(value);
        redisTemplate.opsForList().leftPush(key, jsonObj);
    }

    public <T> T popList(String key){
        String jsonObj = redisTemplate.opsForList().rightPop(key).toString();
        if (jsonObj != null && !jsonObj.equals("")){
            return (T)JSON.parseObject(jsonObj, clz);
        }
        return null;
    }

    public List<T> findAll(String key){
        Long size = redisTemplate.opsForList().size(key);
        List range = redisTemplate.opsForList().range(key, 0, size);
        LinkedList<T> users = new LinkedList<>();
        range.stream().forEach(s->{
            if (s!=null){
                T t = JSON.parseObject(s.toString(), clz);
                users.add(t);
            }
        });
        return users;
    }


    public void zadd(String key, Object object, Long score){
        redisTemplate.opsForZSet().add(key, JSON.toJSONString(object), score);
    }


    public Set getSet(String key){
        Long size = redisTemplate.opsForZSet().size(key);
        Set range = redisTemplate.opsForZSet().range(key, 0, size);
        return range;
    }

    public Set<ZSetOperations.TypedTuple> getScore(String key){
        Long size = redisTemplate.opsForZSet().size(key);
        Set<ZSetOperations.TypedTuple> set = redisTemplate.opsForZSet().rangeWithScores(key, 0, size);
        return set;
    }


    public void addBit(String key, Long offset, Boolean state){
        redisTemplate.execute(new RedisCallback() {
            @Nullable
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.setBit(key.getBytes(), offset, state);
            }
        });
    }

    public Boolean getBit(String key, Long offset){
       return (Boolean) redisTemplate.execute((RedisCallback<Boolean>) con -> con.getBit(key.getBytes(), offset));
    }

    public Long bitCount(String key){
        Object object = redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes()));
        return (Long) object;
    }

    public void del(String key){
        redisTemplate.delete(key);
    }


    public Long size(String key){
        return redisTemplate.opsForList().size(key);
    }
}
