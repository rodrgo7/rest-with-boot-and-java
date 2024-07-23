package com.oliveiradev.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.oliveiradev.data.vo.v1.security.AccountCredentialsVO;
import com.oliveiradev.data.vo.v1.security.TokenVO;
import com.oliveiradev.repositories.UserRepository;
import com.oliveiradev.security.jwt.JwtTokenProvider;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository repository;

    @SuppressWarnings("rawtypes")
    public ResponseEntity signin(AccountCredentialsVO data) {
        try {
            var username = data.getUsername();
            var password = data.getPassword();
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            var user = repository.findByUsername(username);

            var tokenResponse = new TokenVO();
            if (user != null) {
                tokenResponse = tokenProvider.createAcessToken(username, user.getRoles());
            }else {
                throw new UsernameNotFoundException("Username " + username + "not found!");
            }

            return ResponseEntity.ok(tokenResponse);
        }catch (Exception e) {
            throw new BadCredentialsException("Invalid username/password supplied!");
        }
    }
}
