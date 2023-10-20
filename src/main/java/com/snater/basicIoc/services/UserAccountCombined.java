package com.snater.basicIoc.services;

import com.snater.basicIoc.annotations.Component;
import com.snater.basicIoc.annotations.Default;
@Component
@Default({AccountService.class , UserService.class })
public class UserAccountCombined implements  AccountService,UserService {

	@Override
	public Long getAccountNumber(String userName) {
		// TODO Auto-generated method stub
		return 666666L;
	}

	@Override
	public String getUserName() {
		// TODO Auto-generated method stub
		return "ziad";
	}

}
