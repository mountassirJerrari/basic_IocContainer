package com.snater.basicIoc.services;
import com.snater.basicIoc.annotations.Component;
import com.snater.basicIoc.annotations.Default;

@Component
public class AccountServiceImpl implements AccountService {


    public Long getAccountNumber(String userName) {
        return 12345689L;
    }
}