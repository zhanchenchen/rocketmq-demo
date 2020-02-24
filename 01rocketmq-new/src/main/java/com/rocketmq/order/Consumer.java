package com.rocketmq.order;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

/**
 * 顺序消息消费
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        // 1.创建消费者Consumer，制定消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group1");
        // 2.指定Nameserver地址
        consumer.setNamesrvAddr("192.168.186.130:9876;192.168.186.132:9876");
        /**
         * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br>
         * 如果非第一次启动，那么按照上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 3.订阅主题Topic和Tag
        consumer.subscribe("order", "tag1");
        /**
         * 设置消息模式(默认集群模式)
         * 1、CLUSTERING集群模式（负载均衡），多个消费者可以共同消费队列中的消息，每个消费者消费的消息不同
         * 2、BROADCASTING广播模式，向多个消费者分发消息，每个消费者消费的消息相同
         */
        consumer.setMessageModel(MessageModel.CLUSTERING);
        // 4.注册回调函数，处理消息
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                for (MessageExt msg : msgs) {
                    System.out.println("[线程名称："+Thread.currentThread().getName()+"]、消息内容："+new String(msg.getBody()));
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        // 5.启动消费者consumer
        consumer.start();
    }
}
