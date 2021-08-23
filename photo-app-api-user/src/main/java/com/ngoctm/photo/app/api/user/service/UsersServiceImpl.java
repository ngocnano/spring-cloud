package com.ngoctm.photo.app.api.user.service;

import com.ngoctm.photo.app.api.user.data.AlbumsServiceClient;
import com.ngoctm.photo.app.api.user.data.UserEntity;
import com.ngoctm.photo.app.api.user.data.UsersRepository;
import com.ngoctm.photo.app.api.user.model.AlbumResponseModel;
import com.ngoctm.photo.app.api.user.share.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    AlbumsServiceClient albumsServiceClient;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    Environment environment;

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        //Mapper
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

        UserEntity response = usersRepository.save(userEntity);

        return modelMapper.map(response, UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        UserEntity userEntity = usersRepository.findByEmail(email);
        if(userEntity == null){
            throw new UsernameNotFoundException("Email not found");
        }
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        return userDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        logger.debug("get user by user id " + userId);
        UserEntity userEntity = usersRepository.findByUserId(userId);
        if(userEntity == null){
            throw new UsernameNotFoundException("User Not Found");
        }
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = modelMapper.map(userEntity, UserDto.class);

        logger.info("call albums service");
        List<AlbumResponseModel> albums = albumsServiceClient.getAlbums(userId);
        userDto.setAlbums(albums);
        return userDto;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserEntity userEntity = usersRepository.findByEmail(s);
        if(userEntity == null){
            throw new UsernameNotFoundException("Email not found");
        }
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
    }
}
