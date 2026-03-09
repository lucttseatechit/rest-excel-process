package com.example.demo.configuration;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ExecutorConfiguration {

    @Bean("excelProcessExecutor")
    public ExecutorService executor() {
        int core = Runtime.getRuntime().availableProcessors();

        return new ThreadPoolExecutor(core,
                core,
                0L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                new ThreadFactory() {
                    private final AtomicInteger count = new AtomicInteger(1);
                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        return new Thread(r, "excel-worker-" + count.getAndIncrement());
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
