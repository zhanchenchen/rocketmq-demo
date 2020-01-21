package com.learn.controller;

import com.learn.component.Producer;
import com.learn.component.ProducerSecond;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RocketMQProducer {
    @Autowired
    private Producer producer;
    @Autowired
    private ProducerSecond producerSecond;
    @GetMapping("/produce")
    public String produce(){
        producer.sendMessage("1234567","order","*","*");
//        producerSecond.sendMessage("7654321","order","*","*");
        return "ok";
    }
}
