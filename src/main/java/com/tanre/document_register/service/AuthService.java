package com.tanre.document_register.service;

import com.tanre.document_register.config.JwtUtil;
import com.tanre.document_register.model.UserEntity;
import com.tanre.document_register.repository.UserRoleRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserRoleRepository roleRepo;
    private final AdminUserService adminUserService;

    public AuthService(AuthenticationManager authManager, JwtUtil jwtUtil, UserRoleRepository roleRepo, AdminUserService adminUserService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.roleRepo = roleRepo;
        this.adminUserService = adminUserService;
    }

    public String loginAndGetToken(String username, String pass) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, pass));

        UserEntity user = adminUserService.findOrCreateUser(username, List.of("USER"));

        List<String> roles = roleRepo.findRoleNamesByUsername(username);

        return jwtUtil.generateToken(username, roles);
    }
}
