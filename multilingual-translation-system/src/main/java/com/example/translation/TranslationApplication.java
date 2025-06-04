package com.example.translation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 工程启动类
 */
@SpringBootApplication
@EnableAsync
public class TranslationApplication {
    public static void main(String[] args) {
        SpringApplication.run(TranslationApplication.class, args);
    }
}