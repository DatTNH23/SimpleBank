package com.simplebank.accounts.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter @ToString
@AllArgsConstructor @NoArgsConstructor
public class Accounts extends BaseEntity{
    @Column(name="customer_id")
    private long customerId;

    @Column(name = "account_number")
    @Id
    private long accountNumber;

    @Column(name="account_type")
    private String accountType;

    @Column(name="branch_address")
    private String branchAddress;
}
