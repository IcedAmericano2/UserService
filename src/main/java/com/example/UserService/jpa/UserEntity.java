package com.example.UserService.jpa;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id") // 외래 키 필드 추가
    private Long product_id;
    
    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false, length = 100)
    private String encryptedPwd;

    @Column(nullable = false)
    private boolean isApproved;
}
