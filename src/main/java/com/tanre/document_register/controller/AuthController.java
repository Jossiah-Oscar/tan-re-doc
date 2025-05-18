package com.tanre.document_register.controller;

import com.tanre.document_register.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/me")
    public Map<String,Object> me(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(s -> s.startsWith("ROLE_") ? s.substring(5) : s)
                .toList();

        return Map.of(
                "username", authentication.getName(),
                "roles", roles
        );
    }
}