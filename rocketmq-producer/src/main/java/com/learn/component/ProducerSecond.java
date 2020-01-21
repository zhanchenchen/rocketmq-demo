package com.learn.component;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class ProducerSecond {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQProducer.class);

    @Value("${rocketmq.config.namesrvAddr}")
    private String namesrvAddr;

    /**
     * 声明并初始化一个producer
     * 需要一个producerGroup 名字作为构造方法的参数，这里为grampus-order
     */
    private final DefaultMQProducer producer = new DefaultMQProducer("producer-test-second");

    /**
     * 启动生产者
     */
    @PostConstruct
    public void start() {

        System.out.println("===============GC状态==========================");
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        garbageCollectorMXBeans.forEach(collector -> {
            System.out.println(collector.getName());
            System.out.println(collector.getCollectionCount());
            System.out.println(collector.getCollectionTime());
        });
        //生产者延在消费者之后执行
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    LOGGER.info("MQ：启动生产者");
                    /**
                     * 设置NameServer地址
                     * 此处应改为实际NameServer地址，多个地址之间用；分隔
                     * NameServer的地址必须有，不一定非得写死在代码里,这里通过配置文件获取
                     */
                    producer.setNamesrvAddr(namesrvAddr);
                    /**
                     * 发送失败重试次数
                     */
                    producer.setRetryTimesWhenSendFailed(100);


                    producer.setRetryTimesWhenSendAsyncFailed(100);

                    /**
                     * 调用start()方法启动一个producer实例
                     */
                    producer.start();
                    timer.cancel();
                } catch (MQClientException e) {
                    LOGGER.error("MQ：启动生产者失败：{}-{}", e.getResponseCode(), e.getErrorMessage());
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }, 2000);

    }

    /**
     * 发送消息
     *
     * @param data  消息内容
     * @param topic 主题
     * @param tags  标签 如不需要消费topic下面的所有消息，通过tag进行消息过滤
     * @param keys  唯一主键
     */
    public void sendMessage(String data, String topic, String tags, String keys) {
        try {

            for (int i = 0; i < 1000; i++) {

                byte[] messageBody = (data+"===sec"+i).getBytes(RemotingHelper.DEFAULT_CHARSET);

                Message mqMsg = new Message(topic, tags, keys, messageBody);
                producer.send(mqMsg, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        LOGGER.info("MQ: 生产者发送消息 {}", sendResult);
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        LOGGER.error(throwable.getMessage(), throwable);
                    }
                });
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

//    /**
//     * 发送消息根据orderId到指定队列，解决消息的顺序问题
//     *
//     * @param data
//     * @param topic
//     * @param tags
//     * @param keys
//     */
//    public void sendMessageByOrderId(String data, String topic, String tags, String keys) {
//        try {
//            if (StringUtils.isBlank(data)) {
//                return;
//            }
//            JSONObject jsonObject = JSONObject.parseObject(data);
//
//            //获取订单id
//            String orderId = jsonObject.getString("orderId");
//            byte[] messageBody = data.getBytes(RemotingHelper.DEFAULT_CHARSET);
//            Message mqMsg = new Message(topic, tags, keys, messageBody);
//            producer.send(mqMsg, new MessageQueueSelector() {
//                @Override
//                public MessageQueue select(List<MessageQueue> list, Message msg, Object arg) {
//                    //按照订单号发送到固定的队列
//                    int index = arg.hashCode() % list.size();
//                    return list.get(index);
//                }
//            }, orderId, new SendCallback() {
//                @Override
//                public void onSuccess(SendResult sendResult) {
//                    LOGGER.info("MQ: 生产者发送消息 {}", sendResult);
//                }
//
//                @Override
//                public void onException(Throwable throwable) {
//                    LOGGER.error(throwable.getMessage(), throwable);
//                }
//            });
//
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//    }

    @PreDestroy
    public void stop() {
        if (producer != null) {
            producer.shutdown();
            LOGGER.info("MQ：关闭生产者");
        }
    }

}
