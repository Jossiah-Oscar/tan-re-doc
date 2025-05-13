package com.tanre.document_register.controller;

import com.tanre.document_register.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> creds){
        try {
            String jwt = authService.loginAndGetToken(
                    creds.get("username"), creds.get("password"));
            return ResponseEntity.ok(Map.of("token", jwt));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401)
                    .body(Map.of("error","invalid_credentials"));
        }
    }
}