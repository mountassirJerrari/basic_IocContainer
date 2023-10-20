package com.snater.basicIoc.services;
import com.snater.basicIoc.annotations.Component;
import com.snater.basicIoc.annotations.Default;

@Component
public class AccountServiceImpl implements AccountService {

	private Long amount;
    public Long getAccountNumber(String userName) {
        return amount;
    }
	@Override
	public void setAmount(int i) {
		amount = (long) i ;
		
	}
}