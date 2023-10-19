package com.snater.basicIoc.services;
import com.snater.basicIoc.annotations.Component;

@Component
public class AccountServiceImpl implements AccountService {


    public Long getAccountNumber(String userName) {
        return 12345689L;
    }
}