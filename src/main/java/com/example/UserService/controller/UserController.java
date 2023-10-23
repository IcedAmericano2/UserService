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

    @GetMapping("/response_userById/{userId}")
    public ResponseEntity<UserResponse> findUserResponseByUserId(@PathVariable("userId") Long userId) {
        UserResponse userResponse = userService.getUserResponseByUserId(userId);
        return ResponseEntity.ok().body(userResponse);
    }

    @GetMapping("/response_userByEmail/{email}")
    public ResponseEntity<UserResponse> findUserResponseByEmail(@PathVariable String email) {
        UserResponse userResponse = userService.findUserResponseByEmail(email);
        return ResponseEntity.ok().body(userResponse);
    }

}

