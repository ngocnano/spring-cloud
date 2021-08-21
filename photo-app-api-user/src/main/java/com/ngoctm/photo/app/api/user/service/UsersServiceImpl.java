package com.ngoctm.photo.app.api.user.service;

import com.ngoctm.photo.app.api.user.data.UserEntity;
import com.ngoctm.photo.app.api.user.data.UsersRepository;
import com.ngoctm.photo.app.api.user.share.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService{

    @Autowired
    UsersRepository usersRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        //Mapper
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPassword("abc");
        usersRepository.save(userEntity);
        return null;
    }
}
