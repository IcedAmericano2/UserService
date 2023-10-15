package com.example.UserService.dto;

import lombok.Getter;

@Getter
public class UserResponse {
    private String email;
    private String name;
    private String phoneNumber;
    private boolean isApproved;

    public UserResponse(String email, String name, String phoneNumber, boolean isApproved) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.isApproved = isApproved;
    }
}
