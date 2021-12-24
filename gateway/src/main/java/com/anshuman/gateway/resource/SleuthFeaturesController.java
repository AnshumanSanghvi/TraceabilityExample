package com.anshuman.gateway.resource;

import com.anshuman.gateway.service.SleuthFeaturesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class SleuthFeaturesController {

    private final SleuthFeaturesService sleuthFeaturesService;

    @RequestMapping("/")
    public String home() {
        log.info("Handling home");
        return "Hello World";
    }

    @GetMapping("/new-thread")
    public String helloSleuthNewThread() {
        return sleuthFeaturesService.newThread();
    }

    @GetMapping("/async")
    public String helloSleuthAsync() throws InterruptedException {
        log.info("Before Async Method Call");
        sleuthFeaturesService.asyncMethod();
        log.info("After Async Method Call");
        return "success";
    }

    @GetMapping("/annotated")
    public String helloAnnotated() {
        log.info("Calling helloAnnotated");
        sleuthFeaturesService.annotated();
        return "success";
    }
}
