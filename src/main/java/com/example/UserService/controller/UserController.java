package com.example.UserService.controller;

import com.example.UserService.dto.UserDto;
import com.example.UserService.service.UserService;
import com.example.UserService.vo.RequestLogin;
import com.example.UserService.vo.RequestUser;
import com.example.UserService.vo.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
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

    @PostMapping("/join")
    public ResponseEntity createUser(@RequestBody RequestUser user){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(user, UserDto.class);
        ResponseUser responseUser = userService.createUser(userDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

}

