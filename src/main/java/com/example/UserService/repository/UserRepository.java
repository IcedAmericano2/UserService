package com.example.UserService.repository;

import com.example.UserService.domain.UserEntity;
import com.example.UserService.dto.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("SELECT new com.example.UserService.dto.UserResponse(u.email, u.name, u.phoneNumber, u.isApproved) FROM UserEntity u WHERE u.email = :email")
    UserResponse findUserResponseByEmail(@PathVariable("email") String email);

}
