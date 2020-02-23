package com.rocketmq.base.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * 发送同步消息
 */
public class SyncProduer {
    public static void main(String[] args) throws Exception {
        // 1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        // 2.指定Nameserver地址
        producer.setNamesrvAddr("192.168.186.130:9876;192.168.186.132:9876");
        // 3.启动producer
        producer.start();
        // 4.创建消息对象，指定主题Topic、Tag和消息体
        for (int i = 0; i < 10; i++) {
            Message message = new Message("base","tag1",("Hello world"+i).getBytes());
            // 5.发送消息
            SendResult result = producer.send(message);
            System.out.println(result);
        }
        // 6.关闭生产者producer
        producer.shutdown();
    }
}
