package com.qyf.jwt.cache;

import com.qyf.jwt.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCache extends RedisCache<User> {

    public User findUserByUserNameAndPwd(String username, String password) {
        List<User> users = super.findAll("user");
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public User findUserById(String id) {
        List<User> users = super.findAll("user");
        for (User u : users) {
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }

}
