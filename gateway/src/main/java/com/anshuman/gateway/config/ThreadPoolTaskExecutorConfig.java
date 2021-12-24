package com.anshuman.gateway.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.instrument.async.LazyTraceExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ThreadPoolTaskExecutorConfig {

    @Autowired
    private BeanFactory beanFactory;

    private static final int CORES = Runtime.getRuntime().availableProcessors();
    private static final String NAME = "CustomTaskExecutor";

    // Creating a custom thread pool executor so that threads and runnables executed with this executor can have span ids and trace ids.
    @Bean("Executor")
    public Executor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORES/2);
        executor.setMaxPoolSize(CORES);
        executor.setQueueCapacity(CORES*2);
        executor.setThreadNamePrefix(NAME);
        executor.initialize();
        // LazyTraceExecutor: This class comes from the Sleuth library and is a special kind of executor
        // that will propagate our traceIds to new threads and create new spanIds in the process.
        return new LazyTraceExecutor(beanFactory, executor);
    }

}

