package com.tanre.document_register.controller;


import com.tanre.document_register.model.UserEntity;
import com.tanre.document_register.service.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminUserController {
    private final AdminUserService admin;

    public AdminUserController(AdminUserService admin) {
        this.admin = admin;
    }

    @PostMapping("/roles")
    public ResponseEntity<?> createRole(@RequestBody Map<String,String> body) {
        String name = body.get("name");
        return ResponseEntity.ok(admin.createRole(name));
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String,Object> body) {
        String username = (String) body.get("username");
        List<String> roles = (List<String>) body.get("roles");
        UserEntity user = admin.createUser(username, roles);
        return ResponseEntity.status(201).body(user);
    }

    @PostMapping("/users/{username}/roles")
    public ResponseEntity<?> addRole(@PathVariable String username,
                                     @RequestBody Map<String,String> body) {
        String role = body.get("role");
        return ResponseEntity.ok(admin.addRoleToUser(username, role));
    }
}
