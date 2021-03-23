package com.qyf.jwt.cache;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;

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

    public Long size(String key){
        return redisTemplate.opsForList().size(key);
    }
}
