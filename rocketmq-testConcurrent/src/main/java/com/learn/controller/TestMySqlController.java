package com.learn.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learn.entity.Demo;
import com.learn.service.IDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/test")
public class TestMySqlController {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IDemoService demoService;

    @GetMapping("save")
    public boolean save(){
        Demo demo = new Demo(UUID.randomUUID().toString().replace("-",""),"小明","1");
        return demoService.save(demo);
    }

    @GetMapping("list")
    public IPage<Demo> list(){
        IPage<Demo> page = new Page<>(1,10);
        IPage<Demo> pageList = demoService.page(page);
        return pageList;
    }

    @GetMapping("redis")
    public String redis(){
//        redisTemplate.opsForValue().set("customer01","testValue");
        String s = (String) redisTemplate.opsForValue().get("customer01");
        System.err.println(s);
        Set customer01 = redisTemplate.keys("customer*");
        System.err.println(customer01);
        return "cc";
    }
}
