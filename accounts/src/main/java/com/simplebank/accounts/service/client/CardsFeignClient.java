package com.simplebank.accounts.service.client;


import com.simplebank.accounts.dto.CardsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "http://cards:9000", fallback = CardsFallback.class)
@Component
public interface CardsFeignClient {
    @GetMapping(value = "/api/fetch",consumes = "application/json")
    public ResponseEntity<CardsDto> fetchCardDetails(@RequestHeader("simplebank-correlation-id")
                                                         String correlationId, @RequestParam String mobileNumber);
}
