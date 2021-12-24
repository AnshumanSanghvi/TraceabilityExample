package com.anshuman.gateway.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

@Service
@AllArgsConstructor
@Slf4j
public class SleuthFeaturesService {

    // Executor bean defined in ThreadPoolTaskExecutorConfig
    private final Executor myExecutor;

    public String newThread() {
        log.info("Inside newThread method, started.");
        // New threads executed with the custom executor will carry over the same trace id,
        // but will have unique spanId.
        myExecutor.execute(() -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("I'm inside the new thread - with a new span");
        });

        log.info("Inside newThread method, I've exited the newly created thread. method ended.");
        return "success";
    }

    // Specify the custom thread pool task executor (ThreadPoolTaskExecutorConfig) with the @Async annotation
    // so that methods that have @Async will use this executor, and consequently
    // they will also have traceId and spanId values.
    @Async("Executor")
    public void asyncMethod() throws InterruptedException {
        log.info("Start Async Method");

        Thread.sleep(1000L);

        log.info("End Async Method");
    }

    @NewSpan // creates a new spanId
    public void annotated() {
        log.info("This method will have a different span from the spanId found at the controller method.");
    }
}
