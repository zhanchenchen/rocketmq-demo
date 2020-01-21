package com.learn.component;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class Consumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Value("${rocketmq.config.namesrvAddr}")
    private String namesrvAddr;


    static Integer count = 0;


    private final DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer-01-test");



    /**
     * 初始化
     *
     * @throws MQClientException
     */
    @PostConstruct
    public void start() {
        try {
            LOGGER.info("MQ：启动消费者");
            /**
             * NameServer的地址和端口，多个逗号分隔开，达到消除单点故障的目的
             */
            consumer.setNamesrvAddr(namesrvAddr);
            /**
             *  批量消费的数量
             *  1.如果consumer先启动，producer发一条consumer消费一条
             *  2.如果consumer后启动，mq堆积数据，consumer每次消费设置的数量
             */
            consumer.setConsumeMessageBatchMaxSize(1);

            /**
             * consumer的消费策略
             * CONSUME_FROM_LAST_OFFSET 默认策略，从该队列最尾开始消费，即跳过历史消息
             * CONSUME_FROM_FIRST_OFFSET 从队列最开始开始消费，即历史消息（还储存在broker的）全部消费一遍
             * CONSUME_FROM_TIMESTAMP 从某个时间点开始消费，和setConsumeTimestamp()配合使用，默认是半个小时以前
             */
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            /**
             * 消费模式：
             * 1.CLUSTERING：集群，默认
             *   同一个Group里每个consumer只消费订阅消息的一部分内容，也就是同一groupName,所有消费的内容加起来才是订阅topic内容的整体，达到负载均衡的目的
             * 2.BROADCASTING:广播模式
             *   同一个Group里每个consumer都能消费到所订阅topic的全部消息，也就是一个消息会被分发多次，被多个consumer消费
             *   广播消息只发送一次，没有重试
             */

            consumer.setMessageModel(MessageModel.CLUSTERING);
            /**
             * 设置consumer所订阅的Topic和Tag，*代表全部的Tag
             */
            consumer.subscribe("order", "*");
            // 注册消息监听器
            consumer.registerMessageListener(new MessageListenerConcurrently() {


                /**
                 * 消费消息
                 *
                 * @param msgs
                 * @param context
                 * @return
                 */
                @Override
                public synchronized ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    int index = 0;

                    try {
                        for (; index < msgs.size(); index++) {
                            MessageExt msg = msgs.get(index);
                            String messageBody = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);

                                LOGGER.info("consumer-02-test收到消息：" + messageBody + "  " + count++);

//                                JSONObject jsonObject = JSONObject.parseObject(messageBody);
//                                Blogs blogs = JSONObject.toJavaObject(jsonObject,Blogs.class);


                        }
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        /**
                         * 重试机制(consumer),仅限于CLUSTERING模式
                         * 1.exception的情况，一般重复16次 10s、30s、1分钟、2分钟、3分钟等等
                         *   获取重试次数：msgs.get(0).getReconsumeTimes()
                         * 2.超时的情况，这种情况MQ会无限制的发送给消费端
                         *   就是由于网络的情况，MQ发送数据之后，Consumer端并没有收到导致超时。也就是消费端没有给我返回return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;这样的就认为没有到达Consumer端
                         */
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    } finally {
                        if (index < msgs.size()) {
                            context.setAckIndex(index + 1);
                        }
                    }
                    /**
                     * 返回消费状态：
                     * CONSUME_SUCCESS 消费成功
                     * RECONSUME_LATER 消费失败，需要稍后重新消费
                     */
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }

            });

            // 启动消费端
            consumer.start();
        } catch (MQClientException e) {
            LOGGER.error("MQ：启动消费者失败：{}-{}", e.getResponseCode(), e.getErrorMessage());
            throw new RuntimeException(e.getMessage(), e);
        }

    }


//     consumer.registerMessageListener(new MessageListenerOrderly() {
//        @Override
//        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
//            int index = 0;
//
//            try {
//                for (; index < msgs.size(); index++) {
//                    MessageExt msg = msgs.get(index);
//                    String messageBody = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);
//
//                    threadPool.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            synchronized (count) {
//                                if ("ggg".equals(msg.getTopic())) {
//                                    //订单消息处理
//                                    LOGGER.info("收到消息：" + messageBody + "  " + count++);
//                                    try {
//                                        test.add();
//                                    } catch (ClassNotFoundException e) {
//                                        LOGGER.error(e.getMessage(), e);
//                                    }
//                                }
//                            }
//
//
//                        }
//                    });
//                }
//            } catch (Exception e) {
//                LOGGER.error(e.getMessage(), e);
//                /**
//                 * 重试机制(consumer),仅限于CLUSTERING模式
//                 * 1.exception的情况，一般重复16次 10s、30s、1分钟、2分钟、3分钟等等
//                 *   获取重试次数：msgs.get(0).getReconsumeTimes()
//                 * 2.超时的情况，这种情况MQ会无限制的发送给消费端
//                 *   就是由于网络的情况，MQ发送数据之后，Consumer端并没有收到导致超时。也就是消费端没有给我返回return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;这样的就认为没有到达Consumer端
//                 */
//                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
//            } finally {
//                if (index < msgs.size()) {
//                    context.getMessageQueue();
//                }
//            }
//            return ConsumeOrderlyStatus.SUCCESS;
//        }
//
//    });


//    public static void main(String[] args) throws InterruptedException, MQClientException {
//        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name");
//        consumer.setNamesrvAddr("localhost:9876");
//        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
//        consumer.subscribe("ddd", "*");
//        consumer.registerMessageListener(new MessageListenerConcurrently() {
//            @Override
//            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
//                                                            ConsumeConcurrentlyContext context) {
//                int index = 0;
//                for (; index < msgs.size(); index++) {
//                            MessageExt msg = msgs.get(index);
//                    String messageBody = null;
//                    try {
//                        messageBody = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//
//                                    synchronized (count) {
//                                        if ("ddd".equals(msg.getTopic())) {
//                                            //订单消息处理
//                                            LOGGER.info("收到消息：" + messageBody + "  " + count++);
//                                        }
//                                    }
//
//
//                        }
//                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//            }
//        });
//        consumer.start();
//        System.out.printf("Consumer Started.%n");
//    }


    @PreDestroy
    public void stop() {
        if (consumer != null) {
            consumer.shutdown();
            LOGGER.error("MQ：关闭消费者");
        }
    }

}
