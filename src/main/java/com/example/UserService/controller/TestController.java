package com.example.UserService.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/authentication_check")
    public String status(){
        return "인증완료";
    }
}
