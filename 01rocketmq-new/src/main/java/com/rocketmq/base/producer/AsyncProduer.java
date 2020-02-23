package com.rocketmq.base.producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * 发送异步消息
 */
public class AsyncProduer {
    public static void main(String[] args) throws Exception {
        // 1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        // 2.指定Nameserver地址
        producer.setNamesrvAddr("192.168.186.130:9876;192.168.186.132:9876");
        // 3.启动producer
        producer.start();
        // 4.创建消息对象，指定主题Topic、Tag和消息体
        for (int i = 0; i < 10; i++) {
            Message message = new Message("base","tag2",("Hello world"+i).getBytes());
            // 5.发送消息
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("发送结果："+sendResult);
                }

                @Override
                public void onException(Throwable throwable) {
                    System.err.println("发送异常："+throwable);
                }
            });
        }
        // 6.关闭生产者producer
//        producer.shutdown();
    }
}
