package com.example.UserService.service;

import com.example.UserService.dto.UserDto;
import com.example.UserService.domain.UserEntity;
import com.example.UserService.repository.UserRepository;
import com.example.UserService.vo.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder pwdEncoder;

    @Override
    public ResponseUser createUser(UserDto userDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(pwdEncoder.encode(userDto.getPwd()));
        userEntity.setApproved(false);

        //이메일 중복 검사
        validateDuplicateMember(userEntity);
        UserEntity save = userRepository.save(userEntity);
        ResponseUser responseUser = mapper.map(save, ResponseUser.class);

        return responseUser;
    }

    @Override
    public Optional<UserEntity> findOne(String email) {
        return userRepository.findByEmail(email);
    }
    

    private void validateDuplicateMember(UserEntity userEntity) {
        userRepository.findByEmail(userEntity.getEmail())
                .ifPresent(m ->{
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

}
