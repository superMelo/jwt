package com.qyf.jwt.web;

import com.qyf.jwt.cache.TokenCache;
import com.qyf.jwt.cache.UserCache;
import com.qyf.jwt.entity.Token;
import com.qyf.jwt.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class LoginController {

    @Autowired
    private UserCache userCache;

    @Autowired
    private TokenCache tokenCache;

    @RequestMapping("loadData")
    public void load(){
        User user = User.builder().id(UUID.randomUUID().toString())
                .username("qyf").password("123").build();
        userCache.pushList("user", user);
    }

    @RequestMapping("login")
    public String login(HttpServletResponse response, String username, String password){
        User user = userCache.findUserByUserNameAndPwd(username, password);
        if (user != null){
            String tokenId = UUID.randomUUID().toString();
            Token token = Token.builder().id(tokenId)
                    .token(tokenId).userId(user.getId())
                    .ip("127.0.0.1")
                    .expTime(new Date()).build();
            tokenCache.pushList("token", token);
            response.addCookie(new Cookie("token", tokenId));
            return "success";
        }
        return "fail";
    }

    @RequestMapping("getUser")
    public User getUser(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")){
                Token token = tokenCache.findTokenById(cookie.getValue());
                String userId = token.getUserId();
                User user = userCache.findUserById(userId);
                return user;
            }
        }
        return null;
    }

    @RequestMapping("findUser")
    public List<User> findUser(){
        List<User> users = userCache.findAll("user");
        return users;
    }

    @RequestMapping("findToken")
    public List<Token> findToken(){
        List<Token> users = tokenCache.findAll("token");
        return users;
    }
}
