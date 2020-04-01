package com.skin.wb.controller;

import com.skin.wb.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: moshiqing
 * @time: 2020/3/26 11:54
 */
@RestController
@RequestMapping("/test")
public class HttpController {

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("redislock")
    public String redislock(String key){
        Boolean lock = redisUtil.lock(key);
        return lock.toString();
    }
}
