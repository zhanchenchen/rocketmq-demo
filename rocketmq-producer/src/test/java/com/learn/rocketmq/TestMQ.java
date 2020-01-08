package com.learn.rocketmq;

import com.learn.component.Producer;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMQ {
    @Autowired
    private Producer producer;

    @Test
    public void testMQ(){
        producer.sendMessage("12312312313","order","*","*");
    }
}
