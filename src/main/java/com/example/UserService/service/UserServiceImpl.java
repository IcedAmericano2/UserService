package com.example.UserService.service;

import com.example.UserService.dto.UserDto;
import com.example.UserService.jpa.UserEntity;
import com.example.UserService.jpa.UserRepository;
import com.example.UserService.vo.RequestLogin;
import com.example.UserService.vo.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


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

        UserEntity save = userRepository.save(userEntity);
        ResponseUser responseUser = mapper.map(save, ResponseUser.class);

        return responseUser;
    }

}
