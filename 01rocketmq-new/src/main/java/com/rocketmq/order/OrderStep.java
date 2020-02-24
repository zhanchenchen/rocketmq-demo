package com.rocketmq.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderStep {
    private Long orderId;
    private String desc;

    public static List<OrderStep> buildOrders() {
        List<OrderStep> list = new ArrayList<>();
        list.add(new OrderStep(1039L, "创建"));
        list.add(new OrderStep(1065L, "付款"));
        list.add(new OrderStep(7235L, "付款"));
        list.add(new OrderStep(7235L, "完成"));
        list.add(new OrderStep(1039L, "完成"));
        list.add(new OrderStep(1065L, "创建"));
        list.add(new OrderStep(1039L, "付款"));
        list.add(new OrderStep(7235L, "创建"));
        list.add(new OrderStep(1065L, "完成"));
        list.add(new OrderStep(1039L, "推送"));
        return list;
    }
}
