package com.simplebank.accounts.service.impl;

import com.simplebank.accounts.dto.AccountsDto;
import com.simplebank.accounts.dto.CardsDto;
import com.simplebank.accounts.dto.CustomerDetailsDto;
import com.simplebank.accounts.dto.LoansDto;
import com.simplebank.accounts.entity.Accounts;
import com.simplebank.accounts.entity.Customer;
import com.simplebank.accounts.exception.ResourceNotFoundException;
import com.simplebank.accounts.mapper.AccountsMapper;
import com.simplebank.accounts.mapper.CustomerMapper;
import com.simplebank.accounts.repository.AccountsRepository;
import com.simplebank.accounts.repository.CustomerRepository;
import com.simplebank.accounts.service.CustomersService;
import com.simplebank.accounts.service.client.CardsFeignClient;
import com.simplebank.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class CustomersServiceImpl implements CustomersService {
    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    /**
     * @param mobileNumber - Input Mobile Number
     * @return Customer Details based on a given mobileNumber
     */
    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);
        customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());

        return customerDetailsDto;

    }
}
