package com.example.UserService.controller;

import com.example.UserService.dto.JWTAuthResponse;
import com.example.UserService.dto.UserResponse;
import com.example.UserService.service.UserService;
import com.example.UserService.vo.RequestLogin;
import com.example.UserService.vo.RequestUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-service")
public class UserController {

    private final Environment env;
    private final UserService userService;

    @GetMapping("/health_check")
    public String status(){
        return "It's Working in User Service";
    }

    @GetMapping("/welcome")
    public String welcome(){
        return env.getProperty("greeting.message");
    }

    @GetMapping("/view")
    public String view() {
        return "redirect:http://localhost:3000";
    }

    @PostMapping("/login")
    public ResponseEntity<JWTAuthResponse> login(@RequestBody RequestLogin requestLogin){
        JWTAuthResponse token = userService.login(requestLogin);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RequestUser requestUser){
        String response = userService.register(requestUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/response_user/{email}")
    public ResponseEntity<UserResponse> getResponseUser(@PathVariable String email) {
        UserResponse userResponse = userService.findUserResponseByEmail(email);
        return ResponseEntity.ok().body(userResponse);
    }
}

