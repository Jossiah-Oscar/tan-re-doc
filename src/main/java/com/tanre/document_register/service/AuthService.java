package com.tanre.document_register.service;

import com.tanre.document_register.config.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    public String loginAndGetToken(String user, String pass) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(user, pass));
        return jwtUtil.generateToken(user);
    }
}
