package com.tuan.ridehub.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    @GetMapping
    public String Hello() {
        return "Hello World!";
    }
}
