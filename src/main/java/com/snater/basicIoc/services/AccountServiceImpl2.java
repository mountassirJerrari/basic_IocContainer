package com.snater.basicIoc.services;
import com.snater.basicIoc.annotations.Component;
import com.snater.basicIoc.annotations.Default;
import com.snater.basicIoc.annotations.Prototype;

@Component
@Default({AccountService.class})
@Prototype
public class AccountServiceImpl2 implements AccountService {


    
	private Long amount;
    public Long getAccountNumber(String userName) {
        return amount;
    }
	@Override
	public void setAmount(int i) {
		amount = (long) i ;
		
	}
}