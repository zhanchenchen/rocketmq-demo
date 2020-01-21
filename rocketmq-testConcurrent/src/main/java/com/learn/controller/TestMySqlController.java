package com.learn.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learn.entity.Demo;
import com.learn.service.IDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/test")
public class TestMySqlController {
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
}
