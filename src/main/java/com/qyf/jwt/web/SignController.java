package com.qyf.jwt.web;


import com.qyf.jwt.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@Slf4j
public class SignController {


    @Autowired
    private UserCache userCache;
    private String time;

    public SignController() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        time = format.format(date);
    }

    @RequestMapping("sign")
    public void sign(Long userId){
        userCache.addBit(time, userId, true);
    }

    @RequestMapping("check")
    public String check(Long userId){
        boolean sign = userCache.getBit(time, userId);
        return sign ? "已签到" : "未签到";
    }
}
