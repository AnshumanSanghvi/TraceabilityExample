package com.anshuman.microservice.aspect;

import com.anshuman.microservice.util.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
@Slf4j
public class TraceIdAspect {

    public static Object addTraceId(ProceedingJoinPoint joinPoint, Map<String, String> headers) throws Throwable
    {
        final Object returnValue;

        // add traceId to the MDC for all log statements that are called from the current thread.
        TraceIdUtil.putTraceIdLogParam(headers);
        try {
            returnValue = joinPoint.proceed();
        } finally {
            MDC.remove("traceId");
        }
        return returnValue;
    }


    // aspect around all Controller methods found in resources package.
    @Around("within(com.anshuman.microservice.resource.*)")
    public Object resourceCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        // assumes the first arg of controller methods is always the headers map.
        // more elegant way could / should be found to make the headers available here.
        Map<String, String> headers = (Map<String, String>) joinPoint.getArgs()[0];
        return addTraceId(joinPoint, headers);
    }


}
