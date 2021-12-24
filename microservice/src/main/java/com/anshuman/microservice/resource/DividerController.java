package com.anshuman.microservice.resource;

import com.anshuman.microservice.service.DivisionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
public class DividerController {

    private final DivisionService divisionService;

    // TraceIdAspect relies on headers being the first parameter of this method
    @GetMapping("/divide")
    public String divide(@RequestHeader Map<String, String> headers,
                         @RequestParam("numerator") int numerator,
                         @RequestParam("denominator") int denominator
    ) {
        log.debug("Request received with numerator: {}, denominator: {}", numerator, denominator);
        String answer = divisionService.divideNumber(numerator, denominator);
        log.debug("answer: {}", answer);
        return answer;
    }
}
