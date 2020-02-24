package com.rocketmq.order;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * 顺序消息生产
 */
public class Producer {
    public static void main(String[] args) throws Exception {
        // 1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        // 2.指定Nameserver地址
        producer.setNamesrvAddr("192.168.186.130:9876;192.168.186.132:9876");
        // 3.启动producer
        producer.start();
        // 4.创建消息对象，指定主题Topic、Tag和消息体
        List<OrderStep> orderSteps = OrderStep.buildOrders();
        for (int i = 0; i < orderSteps.size(); i++) {
            Message message = new Message("order","tag1",orderSteps.get(i).toString().getBytes());
            SendResult result = producer.send(message, new MessageQueueSelector() {
                /**
                 * 消息队列选择器(指定一个消息队列)
                 * @param msq 从给出的消息队列中选一个
                 * @param message 要发送的消息
                 * @param args 业务标识参数(外部构造方法传入)
                 * @return 要发送的队列
                 */
                @Override
                public MessageQueue select(List<MessageQueue> msq, Message message, Object args) {
                    long orderId = (long) args;
                    long index = orderId % msq.size(); //取模
                    return msq.get((int) index);
                }
            }, orderSteps.get(i).getOrderId());
            System.out.println(result);
        }
        // 6.关闭生产者producer
        producer.shutdown();
    }
}
