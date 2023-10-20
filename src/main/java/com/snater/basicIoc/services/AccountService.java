package com.snater.basicIoc.services;

public interface AccountService {
	
    Long getAccountNumber(String userName);
    
	void setAmount(int i);
}