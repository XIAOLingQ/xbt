package com.xbt.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xbt.server.mapper")
public class XbtApplication {
    public static void main(String[] args) {
        SpringApplication.run(XbtApplication.class, args);
    }
}