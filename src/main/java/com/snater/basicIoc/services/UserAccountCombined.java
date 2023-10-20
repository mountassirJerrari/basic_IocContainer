package com.snater.basicIoc.services;

import com.snater.basicIoc.annotations.Component;
import com.snater.basicIoc.annotations.Default;
@Component
@Default({ UserService.class  })
public class UserAccountCombined implements  AccountService,UserService {
	
	public UserAccountCombined() {
		
	}
	private Long amount;
    public Long getAccountNumber(String userName) {
        return amount;
    }
	@Override
	public void setAmount(int i) {
		amount = (long) i ;
		
	}
	@Override
	public String getUserName() {
		// TODO Auto-generated method stub
		return "ziad";
	}
	
	

}
