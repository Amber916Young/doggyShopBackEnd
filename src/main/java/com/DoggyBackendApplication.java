package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.doggy.mapper")
@SpringBootApplication(scanBasePackages={"com.doggy"})
public class DoggyBackendApplication {

    public static void main(String[] args) {
        System.out.println("start");
        SpringApplication.run(DoggyBackendApplication.class, args);
        System.out.println("end");

    }

}
