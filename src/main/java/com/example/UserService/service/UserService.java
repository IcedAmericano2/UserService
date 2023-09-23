package com.example.UserService.service;

import com.example.UserService.dto.UserDto;
import com.example.UserService.vo.ResponseUser;

public interface UserService {
    ResponseUser createUser(UserDto userDto);
}
