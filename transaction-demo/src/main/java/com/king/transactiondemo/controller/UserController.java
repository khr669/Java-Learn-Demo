package com.king.transactiondemo.controller;

import com.king.transactiondemo.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/wrong")
    public String wrong() {
        try {
            userService.createUsersWrong();
        } catch (Exception e) {
            return "Exception caught: " + e.getMessage();
        }
        return "Success";
    }
}
