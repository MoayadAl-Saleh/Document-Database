package com.Moayad.masternode.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController
{
    AuthenticationService authenticationService;

    //constructor
    @Autowired
    public AuthenticationController (AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    //create user
    @PostMapping("/createUser/{userName}/{password}")
    public void createUser (@PathVariable String userName, @PathVariable String password)
    {
        authenticationService.createUser (userName, password);
    }

    //check user and password is correct
    @PostMapping("/isUserCorrect/{userName}/{password}")
    public String isUserCorrect (@PathVariable String userName, @PathVariable String password)
    {
        if (authenticationService.isUserCorrect (userName, password)) return "true";
        else return "false";
    }
}
