package com.reddish.adonisbase;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.reddish.adonisbase.DAO")
public class AdonisBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdonisBaseApplication.class, args);
    }

}
