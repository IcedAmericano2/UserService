package com.example.UserService.service;

import com.example.UserService.domain.UserEntity;
import com.example.UserService.dto.JWTAuthResponse;
import com.example.UserService.dto.UserResponse;
import com.example.UserService.vo.RequestLogin;
import com.example.UserService.vo.RequestUser;

import java.util.Optional;

public interface UserService{

    Optional<UserEntity> findOne(String email);
    JWTAuthResponse login(RequestLogin requestLogin);
    String register(RequestUser requestUser);
    UserResponse findUserResponseByEmail(String email);
}
