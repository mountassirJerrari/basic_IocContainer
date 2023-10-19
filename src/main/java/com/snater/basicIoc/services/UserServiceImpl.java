package com.snater.basicIoc.services;

import com.snater.basicIoc.annotations.Component;

@Component
public class UserServiceImpl implements UserService {

    public String getUserName() {
        return "username";
    }
}