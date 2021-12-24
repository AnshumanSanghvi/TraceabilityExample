package com.anshuman.microservice.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@UtilityClass
@Slf4j
public class TraceIdUtil {

    // get traceId value from the traceId related header
    public static String getTraceId(Map<String, String> headers)
    {
        return headers.keySet()
                .stream()
                .filter(key -> key.contains("TraceId"))
                .peek(key -> log.trace("found traceId header {}", key))
                .findFirst()
                .map(headers::get)
                .map(list -> String.join(" ", list))
                .orElse("n/a");
    }

    // add traceId key and value in the MDC map
    public static void putTraceIdLogParam(Map<String, String> headers)
    {
        Optional.of(TraceIdUtil.getTraceId(headers))
                .filter(Predicate.not(String::isEmpty))
                .ifPresentOrElse(traceId-> MDC.put("traceId", "(traceId: " + traceId + ")"),
                        () -> log.warn("traceId not found/parsed"));
    }
}
