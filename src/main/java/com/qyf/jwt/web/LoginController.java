package com.qyf.jwt.web;

import com.qyf.jwt.cache.TokenCache;
import com.qyf.jwt.cache.UserCache;
import com.qyf.jwt.entity.Key;
import com.qyf.jwt.entity.Token;
import com.qyf.jwt.entity.User;
import com.qyf.jwt.util.RSACrypt;
import org.apache.tomcat.util.codec.binary.Base64;
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

    @Autowired
    private Key key;

    @RequestMapping("loadData")
    public void load(){
        User user = User.builder().id(UUID.randomUUID().toString())
                .username("qyf").password("123").build();
        userCache.pushList("user", user);
    }

    @RequestMapping("login")
    public String login(HttpServletResponse response, String username, String password) throws Exception{
        User user = userCache.findUserByUserNameAndPwd(username, password);
        if (user != null){
            String tokenId = UUID.randomUUID().toString().replace("-", "");
            Token token = Token.builder().id(tokenId)
                    .token(tokenId).userId(user.getId())
                    .ip("127.0.0.1")
                    .expTime(new Date()).build();
            tokenCache.pushList("token", token);
            //前端加密
            String publicKey = key.getPublicKey();
            byte[] code2 = RSACrypt.encryptByPublicKey(Base64.decodeBase64(tokenId.getBytes()), Base64.decodeBase64(publicKey));
            response.addCookie(new Cookie("token", Base64.encodeBase64String(code2)));
            return "success";
        }
        return "fail";
    }

    @RequestMapping("getUser")
    public User getUser(HttpServletRequest request) throws Exception {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")){
                String value = cookie.getValue();
                String privateKey = key.getPrivateKey();
                byte[] id = RSACrypt.decryptByPrivateKey( Base64.decodeBase64(value.getBytes()), Base64.decodeBase64(privateKey));
                Token token = tokenCache.findTokenById(Base64.encodeBase64String(id));
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
