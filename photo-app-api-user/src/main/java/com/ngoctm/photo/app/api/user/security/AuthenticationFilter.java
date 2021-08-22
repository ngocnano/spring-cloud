package com.ngoctm.photo.app.api.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngoctm.photo.app.api.user.model.LoginRequestModel;
import com.ngoctm.photo.app.api.user.service.UsersService;
import com.ngoctm.photo.app.api.user.share.UserDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UsersService usersService;
    private Environment environment;

    public AuthenticationFilter(UsersService usersService, Environment environment){
        this.usersService = usersService;
        this.environment = environment;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            LoginRequestModel loginRequestModel = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginRequestModel.class);
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                        loginRequestModel.getEmail(),
                        loginRequestModel.getPassword(),
                        new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.attemptAuthentication(request, response);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String username = ((User) authResult.getPrincipal()).getUsername();
        UserDto userDto = usersService.getUserByEmail(username);
        String token = Jwts.builder()
                .setSubject(userDto.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, environment.getProperty("token.secret"))
                .compact();
        response.addHeader("token", token);
        response.addHeader("userId", userDto.getUserId());
    }


}
