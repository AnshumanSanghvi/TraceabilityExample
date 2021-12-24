package com.anshuman.microservice.filter;

import com.anshuman.microservice.util.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HttpRequestLoggingFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain)
    {
        ServerHttpRequest req = exchange.getRequest();

        // intercept http requests to log them, including if they have a traceId related header.
        log.trace("Request[method: {}, path: {}, id: {}{}{}{}]",
                req.getMethod(), req.getPath(), req.getId(),
                getQueryParams(req), getRemoteAddress(req), getTraceId(req));

        return chain.filter(exchange);
    }

    private static String getQueryParams(ServerHttpRequest req)
    {
        return Optional.of(req.getQueryParams())
                .map(MultiValueMap::entrySet)
                .filter(Predicate.not(Set::isEmpty))
                .map(paramSet -> ", queryParams: [" + paramSet
                        .stream()
                        .map(e -> e.getKey() + ":" + e.getValue())
                        .collect(Collectors.joining(",")) + "]")
                .orElse("");
    }

    private static String getRemoteAddress(ServerHttpRequest req)
    {
        return Optional
                .ofNullable(req.getRemoteAddress())
                .filter(address -> !address.isUnresolved())
                .map(address -> ", address: " + address.getAddress() + ":" + address.getPort())
                .orElse("");
    }

    private static String getTraceId(ServerHttpRequest req)
    {
        return Optional.of(req.getHeaders().toSingleValueMap())
                .map(TraceIdUtil::getTraceId)
                .filter(Predicate.not(String::isEmpty))
                .map(traceId -> ", traceId: " + traceId)
                .orElse("");
    }
}
