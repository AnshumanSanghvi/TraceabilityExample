package com.anshuman.microservice.aspect;

import com.anshuman.microservice.util.FormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Aspect
@Component
@Slf4j
public class ProfilingAspect {

    @Around("within(com.anshuman.microservice.resource.*)")
    public Object resourceCalls(ProceedingJoinPoint joinPoint) throws Throwable
    {
        return loggingMethod(joinPoint, "Resource Call");
    }

    public static Object loggingMethod(ProceedingJoinPoint joinPoint, String source) throws Throwable
    {
        final String methodName = joinPoint.getSignature().toShortString();
        Instant start = Instant.now();
        final Object returnValue;
        try
        {
            returnValue = joinPoint.proceed();
        } catch (Throwable ex)
        {
            logDuration(source, methodName, start, "exceptionally");
            throw ex;
        }
        logDuration(source, methodName, start, "successfully");
        return returnValue;
    }

    private static void logDuration(String source, String methodName, Instant start, String success) {
        Duration duration = Duration.between(start, Instant.now());
        MDC.put("duration", "(duration: " + FormatUtil.humanReadableTime(duration) + ")");
        log.trace(source + ": method: {} request completed {}", methodName, success);
        MDC.remove("duration");
    }
}
