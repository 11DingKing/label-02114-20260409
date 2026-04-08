package com.menu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.menu.mapper")
public class GirlfriendMenuApplication {
    public static void main(String[] args) {
        SpringApplication.run(GirlfriendMenuApplication.class, args);
    }
}
