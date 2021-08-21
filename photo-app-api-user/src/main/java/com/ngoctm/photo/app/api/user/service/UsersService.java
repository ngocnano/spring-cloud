package com.ngoctm.photo.app.api.user.service;

import com.ngoctm.photo.app.api.user.share.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsersService extends UserDetailsService {

    UserDto createUser(UserDto userDto);

    UserDto getUserByEmail(String email);
}
