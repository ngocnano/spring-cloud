package com.ngoctm.photo.app.api.user.controller;

import com.ngoctm.photo.app.api.user.model.AlbumResponseModel;
import com.ngoctm.photo.app.api.user.model.UserRequestModel;
import com.ngoctm.photo.app.api.user.model.UserResponseModel;
import com.ngoctm.photo.app.api.user.service.UsersService;
import com.ngoctm.photo.app.api.user.share.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class Usercontroller {

    @Autowired
    Environment env;

    @Autowired
    UsersService usersService;

    @GetMapping("/status/check")
    public String status(){
        return "working " + env.getProperty("local.server.port") + " " + env.getProperty("token.expiration_time");
    }

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
                produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserResponseModel> create(@Valid @RequestBody UserRequestModel user){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(user, UserDto.class);

        UserDto userDtoResponse = usersService.createUser(userDto);
        UserResponseModel valueResponse = modelMapper.map(userDtoResponse, UserResponseModel.class);
        return new ResponseEntity(valueResponse, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{userId}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserResponseModel> getUser(@PathVariable("userId") String userId){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = usersService.getUserByUserId(userId);
        UserResponseModel userResponseModel = modelMapper.map(userDto, UserResponseModel.class);
        return new ResponseEntity<UserResponseModel>(userResponseModel, HttpStatus.OK);
    }

}
