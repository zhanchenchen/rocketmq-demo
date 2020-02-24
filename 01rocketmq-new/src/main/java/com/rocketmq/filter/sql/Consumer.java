package com.rocketmq.filter.sql;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

public class Consumer {
    public static void main(String[] args) throws Exception {
        // 1.创建消费者Consumer，制定消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group1");
        // 2.指定Nameserver地址
//        consumer.setNamesrvAddr("192.168.186.130:9876;192.168.186.132:9876");
        consumer.setNamesrvAddr("123.206.180.111:9876");
        // 3.订阅主题Topic和消息选择器
        consumer.subscribe("filterSql", MessageSelector.bySql("i>5"));
        /**
         * 设置消息模式(默认集群模式)
         * 1、CLUSTERING集群模式（负载均衡），多个消费者可以共同消费队列中的消息，每个消费者消费的消息不同
         * 2、BROADCASTING广播模式，向多个消费者分发消息，每个消费者消费的消息相同
         */
        consumer.setMessageModel(MessageModel.CLUSTERING);
        // 4.注册回调函数，处理消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            // 接受消息内容
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for (MessageExt msg : list) {
                    System.out.printf("%s Receive New Messages: %s %n",
                            Thread.currentThread().getName(), new String(msg.getBody()));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // 5.启动消费者consumer
        consumer.start();
    }
}
