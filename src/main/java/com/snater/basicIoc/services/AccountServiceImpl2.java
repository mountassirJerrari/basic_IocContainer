package com.snater.basicIoc.services;
import com.snater.basicIoc.annotations.Component;
import com.snater.basicIoc.annotations.Default;

@Component

public class AccountServiceImpl2 implements AccountService {


    public Long getAccountNumber(String userName) {
        return 2222222L;
    }
}