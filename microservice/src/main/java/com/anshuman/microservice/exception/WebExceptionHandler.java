package com.anshuman.microservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class WebExceptionHandler {

    // add the root cause of all exceptions in the error logs via MDC so that this information is
    // immediately visible, regardless of how many times this exception was wrapped in another exception.
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void internalServerError(Exception e) {
        Throwable rootCause = getRootCause(e);
        MDC.put("rootCause", rootCause.getClass().getName());
        log.error("Internal Server Error. message={}", rootCause.getMessage());
        MDC.remove("rootCause");
    }

    // get the rootest of all root causes
    private Throwable getRootCause(Exception e) {
        Throwable rootCause = e;
        int count = 5;
        while (e.getCause() != null && rootCause.getCause() != rootCause && count !=0) {
            rootCause = e.getCause();
            count--;
        }
        return rootCause;
    }

}