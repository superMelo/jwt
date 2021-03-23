package com.qyf.jwt.cache;

import com.qyf.jwt.entity.Token;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenCache extends RedisCache<Token> {

    public Token findTokenById(String id){
        if (size("token") > 2){
            return (Token) popList("token");
        }
        List<Token> tokens = findAll("token");
        for (Token token : tokens) {
            if (token.getId().equals(id)){
                return token;
            }
        }
        return null;
    }
}
