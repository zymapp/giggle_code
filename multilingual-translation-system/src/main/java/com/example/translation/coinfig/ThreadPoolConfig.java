package com.example.translation.coinfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * 线程池配置
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * 语音识别线程池配置，这里请根据自己服务器的配置进行设置
     */
    @Bean(name = "whisperTaskExecutor")
    public ExecutorService whisperTaskExecutor() {
        return new ThreadPoolExecutor(
                4, // 核心线程数
                8, // 最大线程数
                60L, TimeUnit.SECONDS, // 空闲线程存活时间
                new LinkedBlockingQueue<>(1000000), // 阻塞队列
                new ThreadFactory() {
                    private int count = 0;

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "WhisperTaskThread-" + count++);
                    }
                },
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

}

