package com.example.UserService.service;

import com.example.UserService.config.JwtTokenProvider;
import com.example.UserService.config.MyUserDetailsService;
import com.example.UserService.dto.JWTAuthResponse;
import com.example.UserService.domain.UserEntity;
import com.example.UserService.dto.UserResponse;
import com.example.UserService.exception.BlogAPIException;
import com.example.UserService.exception.BusinessLogicException;
import com.example.UserService.exception.ExceptionCode;
import com.example.UserService.repository.UserRepository;
import com.example.UserService.vo.RequestLogin;
import com.example.UserService.vo.RequestUser;
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
    private final MyUserDetailsService myUserDetailsService;
    private final RedisService redisService;

    @Override
    public Optional<UserEntity> findOne(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public JWTAuthResponse login(RequestLogin requestLogin) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                requestLogin.getEmail(), requestLogin.getPwd()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Long userId = myUserDetailsService.findUserIdByEmail(requestLogin.getEmail());
        JWTAuthResponse token = jwtTokenProvider.generateToken(requestLogin.getEmail(), authentication, userId);
        return token;
    }

    @Override
    public String register(RequestUser requestUser) {

        // add check for email exists in database
        if(userRepository.existsByEmail(requestUser.getEmail())){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Email is already exists!.");
        }

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(requestUser, UserEntity.class);
        userEntity.setEncryptedPwd(pwdEncoder.encode(requestUser.getPwd()));
        userEntity.setApproved(false);
        userRepository.save(userEntity);

        return "User registered successfully!.";
    }

    @Override
    public UserResponse getUserResponseByUserId(Long userId) {
        return userRepository.findUserResponseByUserId(userId);
    }

    @Override
    public UserResponse findUserResponseByEmail(String email) {
        return userRepository.findUserResponseByEmail(email);
    }

    @Override
    public JWTAuthResponse reissueAccessToken(String refreshToken) {
        this.verifiedRefreshToken(refreshToken);
        String email = jwtTokenProvider.getEmail(refreshToken);
        String redisRefreshToken = redisService.getValues(email);

        if (redisService.checkExistsValue(redisRefreshToken) && refreshToken.equals(redisRefreshToken)) {
            Optional<UserEntity> findUser = this.findOne(email);
            UserEntity userEntity = UserEntity.of(findUser);
            JWTAuthResponse tokenDto = jwtTokenProvider.generateToken(email, jwtTokenProvider.getAuthentication(refreshToken), userEntity.getId());
            String newAccessToken = tokenDto.getAccessToken();
            long refreshTokenExpirationMillis = jwtTokenProvider.getRefreshTokenExpirationMillis();
            return tokenDto;
        } else throw new BusinessLogicException(ExceptionCode.TOKEN_IS_NOT_SAME);
    }

    private void verifiedRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new BusinessLogicException(ExceptionCode.HEADER_REFRESH_TOKEN_NOT_EXISTS);
        }
    }
}
