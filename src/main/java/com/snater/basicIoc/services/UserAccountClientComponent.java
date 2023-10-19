package com.snater.basicIoc.services;
import com.snater.basicIoc.annotations.Autowired;
import com.snater.basicIoc.annotations.Component;
import com.snater.basicIoc.annotations.Qualifier;

/**
 * Client class, having userService and accountService expected to initialized by
 * CustomInjector.java
 */
@Component
public class UserAccountClientComponent {

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier(value = "accountServiceImpl")
    private AccountService accountService;

    public void displayUserAccount() {
        String username = userService.getUserName();
        Long accountNumber = accountService.getAccountNumber(username);
        System.out.println("\n\tUser Name: " + username + "\n\tAccount Number: " + accountNumber);
    }
}