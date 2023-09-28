package com.example.UserService.service;

import com.example.UserService.domain.UserEntity;
import com.example.UserService.dto.UserDto;
import com.example.UserService.vo.ResponseUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {

    ResponseUser createUser(UserDto userDto);
    Optional<UserEntity> findOne(String email);
}
