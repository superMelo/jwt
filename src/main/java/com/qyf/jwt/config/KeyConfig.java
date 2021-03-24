package com.qyf.jwt.config;

import com.qyf.jwt.entity.Key;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyConfig {

    @Value("${rsa.publicKey}")
    public  String publicKey;

    @Value("${rsa.privateKey}")
    public  String privateKey;


    @Bean
    public Key key(){
        Key key = new Key();
        key.setPublicKey(publicKey);
        key.setPrivateKey(privateKey);
        return key;
    }
}
