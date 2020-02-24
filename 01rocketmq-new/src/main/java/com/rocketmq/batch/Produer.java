package com.rocketmq.batch;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 发送批量消息
 */
public class Produer {
    public static void main(String[] args) throws Exception {
        // 1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        // 2.指定Nameserver地址
//        producer.setNamesrvAddr("192.168.186.130:9876;192.168.186.132:9876");
        producer.setNamesrvAddr("123.206.180.111:9876");
        // 3.启动producer
        producer.start();
        Random random = new Random();
        // 4.创建消息对象，指定主题Topic、Tag和消息体
        List<Message> msgList = new ArrayList<>(5);
        msgList.add(new Message("batch", "tag1", "Hello world".getBytes()));
        msgList.add(new Message("batch", "tag1", "Hello world".getBytes()));
        msgList.add(new Message("batch", "tag1", "Hello world".getBytes()));
        msgList.add(new Message("batch", "tag1", "Hello world".getBytes()));
        msgList.add(new Message("batch", "tag1", "Hello world".getBytes()));
        // 5.发送消息
        SendResult result = producer.send(msgList);
        System.out.println(result);
        // 6.关闭生产者producer
        producer.shutdown();
    }
}
