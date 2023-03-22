package com.reddish.adonis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.reddish.adonis.mapper")
public class AdonisApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdonisApplication.class, args);
    }

}
