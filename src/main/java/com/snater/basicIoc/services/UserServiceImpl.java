package com.snater.basicIoc.services;

import com.snater.basicIoc.annotations.Component;
import com.snater.basicIoc.annotations.Default;

@Component


public class UserServiceImpl implements UserService {

    public String getUserName() {
        return "username";
    }
}