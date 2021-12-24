package com.anshuman.microservice.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class Controller {

    // TraceIdAspect relies on headers being the first parameter of this method
    @GetMapping("/test")
    public String test(@RequestHeader Map<String, String> headers)
    {
        log.debug("Request received for /test");
        return "Hello from WebService";
    }

}
