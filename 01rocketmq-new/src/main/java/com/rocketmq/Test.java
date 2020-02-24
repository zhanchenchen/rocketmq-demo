package com.rocketmq;

import java.util.Random;

public class Test {
    public static void main(String[] args) {
        Random random = new Random(2);
        for (int i = 0; i < 20; i++) {
            System.out.println(random.nextInt(3));
        }
    }
}
