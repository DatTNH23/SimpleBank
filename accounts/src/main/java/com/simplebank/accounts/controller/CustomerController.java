package com.simplebank.accounts.controller;
import com.simplebank.accounts.dto.CustomerDetailsDto;
import com.simplebank.accounts.dto.ErrorResponseDto;
import com.simplebank.accounts.service.CustomersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "REST API for Customers in SimpleBank",
        description = "REST APIs in Simple to FETCH customer details"
)
@RestController
@RequestMapping(path="/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomersService customersService;

    public CustomerController(CustomersService customersService){
        this.customersService = customersService;
    }

    @Operation(
            summary = "Fetch Customer Details REST API",
            description = "REST API to fetch Customer details based on a mobile number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping("/fetchCustomerDetails")
    public ResponseEntity<CustomerDetailsDto> fetchCustomerDetails(@RequestHeader("simplebank-correlation-id")
                                                                       String correlationId,
                                                                   @RequestParam
                                                                   @Pattern(regexp="(^$|[0-9]{10})",
                                                                           message = "Mobile number must be 10 digits")
                                                                   String mobileNumber){
        logger.debug("fetchCustomerDetails start");
        CustomerDetailsDto customerDetailsDto = customersService.fetchCustomerDetails(mobileNumber,correlationId);
        return ResponseEntity.status(HttpStatus.SC_OK).body(customerDetailsDto);

    }
}
