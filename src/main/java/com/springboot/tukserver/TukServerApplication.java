package com.springboot.tukserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class TukServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TukServerApplication.class, args);
    }

}
