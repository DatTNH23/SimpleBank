package com.simplebank.accounts.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CustomerDto {
    @NotEmpty(message = "Name can not be a null or empty")
    @Size(min = 5, max = 30, message = "The length of the customer name should be between 5 and 30")
    private String name;
    @NotEmpty
    private String email;
    private String mobileNumber;
    private AccountsDto accountsDto;
}
