package com.simplebank.accounts.service;

import com.simplebank.accounts.dto.CustomerDetailsDto;

public interface CustomersService {
    /**
     *
     * @param mobileNumber - Input Mobile Number
     * @return Customer Details based on a given mobileNumber
     */
    CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId);
}
