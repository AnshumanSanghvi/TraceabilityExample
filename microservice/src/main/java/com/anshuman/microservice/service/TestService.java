package com.anshuman.microservice.service;

import com.anshuman.microservice.util.FormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TestService {

    public void threadContextTest() throws InterruptedException {
        MDC.clear();
        ThreadContext.clearAll();

        // add to thread context
        ThreadContext.put("threadName", Thread.currentThread().getName());
        log.info("Entered method threadContextTest. ThreadContext: {}", FormatUtil.stringifyMap(ThreadContext.getContext()));

        // check if something added to ThreadContext is automatically available on MDC: YES
        ThreadContext.put("traceId", "traceId:12345");
        // since log4j2.xml has %X{traceId} in the log format, it should show up twice in the log statement? YES
        log.info("Added thread context data. ThreadContext: {}", FormatUtil.stringifyMap(ThreadContext.getContext()));

        // check if ThreadContext carries over to a different thread: NO
        Thread thread = new Thread(() -> {
            ThreadContext.put("hello", "world");
            log.info("Running on a thread spun up from threadContextTest method. ThreadContext: {}", FormatUtil.stringifyMap(ThreadContext.getContext()));
        });
        thread.start();
        thread.join();

        // check if data added to thread context of a separate thread is carried over to original thread: NO
        log.info("returned back to original thread in threadContextTest method. ThreadContext: {}", FormatUtil.stringifyMap(ThreadContext.getContext()));

        // check if clearing MDC will clear ThreadContext: YES
        MDC.clear();
        log.info("Cleared ThreadContext and exiting method threadContextTest. ThreadContext: {}", FormatUtil.stringifyMap(ThreadContext.getContext()));
    }


    public void threadPoolExecutorTest() throws InterruptedException {
        Collection<Callable<Void>> callables = new ArrayList<>(38);
        for (int i = 1; i <= 30; i++) {
            String name = "callable" + i;
            Callable<Void> callable = () -> {
                MDC.put(name, name);
                log.info("Callable name: {}, ThreadContext: {}", name, FormatUtil.stringifyMap(ThreadContext.getContext()));
                return null;
            };
            callables.add(callable);
        }

        // each thread will have its own thread context, but if you reuse a thread,
        // then the threadContext will retain from previous execution of the thread.
        Executors.newCachedThreadPool().invokeAll(callables);

        // creating a threadPool executor that clears the threadContext by overriding the afterExecute method (See MDCAwareThreadPoolExecutor) will fix this issue.
        ExecutorService executor = new MDCAwareThreadPoolExecutor(3, 3, 0, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), Thread::new, new ThreadPoolExecutor.AbortPolicy());
        executor.invokeAll(callables);

    }
}

@Slf4j
class MDCAwareThreadPoolExecutor extends ThreadPoolExecutor {

    public MDCAwareThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        log.debug("Cleaning the MDC context on thread execution completed");
        MDC.clear();
        ThreadContext.clearAll();
    }
}
