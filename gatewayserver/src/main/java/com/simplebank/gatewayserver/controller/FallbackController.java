package com.simplebank.gatewayserver.controller;

import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    public Mono<String> contactSupport(){
        return Mono.just("An error occurred. Please try after some time");
    }
}
