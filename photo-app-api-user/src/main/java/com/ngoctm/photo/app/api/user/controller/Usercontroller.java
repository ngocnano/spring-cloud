package com.ngoctm.photo.app.api.user.controller;

import com.ngoctm.photo.app.api.user.model.User;
import com.ngoctm.photo.app.api.user.service.UsersService;
import com.ngoctm.photo.app.api.user.share.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class Usercontroller {

    @Autowired
    Environment env;

    @Autowired
    UsersService usersService;

    @GetMapping("/status/check")
    public String status(){
        return "working " + env.getProperty("local.server.port");
    }

    @PostMapping
    public String create(@Valid @RequestBody User user){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        usersService.createUser(userDto);
        return null;
    }

}
