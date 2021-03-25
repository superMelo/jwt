package com.qyf.jwt.web;

import com.alibaba.fastjson.JSON;
import com.qyf.jwt.cache.UserCache;
import com.qyf.jwt.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class SortController {

    @Autowired
    private UserCache userCache;

    private  AtomicLong i = new AtomicLong(1);

    @RequestMapping("addUser")
    public void addUser(User user){
        userCache.zadd("users", JSON.toJSONString(user), i.incrementAndGet());
    }

    @RequestMapping("getUserScore")
    public Set<ZSetOperations.TypedTuple> getUserScore(){
        Set<ZSetOperations.TypedTuple> users = userCache.getScore("users");
        return users;
    }
}
