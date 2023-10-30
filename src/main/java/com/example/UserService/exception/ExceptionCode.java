package com.example.UserService.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "Member Not Found"),
    RESOURCE_NOT_FOUND(404, "Resource Not Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    INVALID_INPUT(422, "Invalid Input"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    TOKEN_IS_NOT_SAME(401, "Access Token and Refresh Token do not match"),
    HEADER_REFRESH_TOKEN_NOT_EXISTS(400, "Refresh Token missing in request header");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
