package com.rocketmq.transaction;

import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * 发送同步消息
 */
public class Produer {
    public static void main(String[] args) throws Exception {
        // 1.创建消息生产者producer，并制定生产者组名
        TransactionMQProducer producer = new TransactionMQProducer("group5");
        // 2.指定Nameserver地址
//        producer.setNamesrvAddr("192.168.186.130:9876;192.168.186.132:9876");
        producer.setNamesrvAddr("123.206.180.111:9876");
        // 注册一个事务监听器
        producer.setTransactionListener(new TransactionListener() {
            /**
             * 在该方法中执行本地事务
             * @param message
             * @param o
             * @return
             */
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                System.out.println("Object:"+o);
                if (StringUtils.equals("tag1",message.getTags())){
                    return LocalTransactionState.COMMIT_MESSAGE;
                } else if(StringUtils.equals("tag2",message.getTags())){
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                } else{
                    return  LocalTransactionState.UNKNOW;
                }
            }

            /**
             * 在该方法中执行事务回查
             * @param messageExt
             * @return
             */
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                System.out.println("消息tag："+messageExt.getTags());
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });
        // 3.启动producer
        producer.start();
        String [] tags = new String[]{"tag1","tag2","tag3"};
        // 4.创建消息对象，指定主题Topic、Tag和消息体
        for (int i = 0; i < 3; i++) {
            Message message = new Message("transaction",tags[i],("Hello world"+i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 5.发送消息
            SendResult result = producer.sendMessageInTransaction(message,"zcc"+i);
            System.out.println(result);
        }
    }
}
