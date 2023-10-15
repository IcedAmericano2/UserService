package com.example.UserService.service;

import com.example.UserService.config.JwtTokenProvider;
import com.example.UserService.dto.JWTAuthResponse;
import com.example.UserService.domain.UserEntity;
import com.example.UserService.dto.UserResponse;
import com.example.UserService.exception.BlogAPIException;
import com.example.UserService.repository.UserRepository;
import com.example.UserService.vo.RequestLogin;
import com.example.UserService.vo.RequestUser;
import com.example.UserService.vo.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder pwdEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Optional<UserEntity> findOne(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public JWTAuthResponse login(RequestLogin requestLogin) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                requestLogin.getEmail(), requestLogin.getPwd()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        JWTAuthResponse token = jwtTokenProvider.generateToken(requestLogin.getEmail(), authentication);
        return token;
    }

    @Override
    public String register(RequestUser requestUser) {
//        // add check for username exists in database
//        if(userRepository.findByEmail(requestUser.getUsername())){
//            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Username is already exists!.");
//        }

        // add check for email exists in database
        if(userRepository.existsByEmail(requestUser.getEmail())){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Email is already exists!.");
        }

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(requestUser, UserEntity.class);
        userEntity.setEncryptedPwd(pwdEncoder.encode(requestUser.getPwd()));
        userEntity.setApproved(false);

//        Set<Role> roles = new HashSet<>();
//        Role userRole = roleRepository.findByName("ROLE_USER").get();
//        roles.add(userRole);
//        user.setRoles(roles);

        userRepository.save(userEntity);

        return "User registered successfully!.";
    }

    @Override
    public UserResponse findUserResponseByEmail(String email) {
        return userRepository.findUserResponseByEmail(email);
    }

}
