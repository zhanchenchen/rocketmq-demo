package com.learn.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@Data
@AllArgsConstructor
public class Demo {
    private String id;
    private String name;
    private String sex;
}
