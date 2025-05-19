package com.tanre.document_register.controller;


import com.tanre.document_register.dto.RoleRequest;
import com.tanre.document_register.dto.RolesRequest;
import com.tanre.document_register.dto.UserCreateRequest;
import com.tanre.document_register.dto.UserDTO;
import com.tanre.document_register.model.RoleEntity;
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

    @GetMapping("/users")
    public List<UserDTO> listUsers() {
        return admin.findAllUsers().stream()
                .map(u -> new UserDTO(
                        u.getUsername(),
                        u.getRoles().stream().map(RoleEntity::getName).toList()
                ))
                .toList();
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateRequest req) {
        UserEntity created = admin.createUser(req.username(), req.roles());
        return ResponseEntity.status(201).body(new UserDTO(
                created.getUsername(),
                created.getRoles().stream().map(RoleEntity::getName).toList()
        ));
    }

    @PutMapping("/users/{username}/roles")
    public UserDTO replaceRoles(
            @PathVariable String username,
            @RequestBody RolesRequest req
    ) {
        UserEntity updated = admin.replaceUserRoles(username, req.roles());
        return new UserDTO(
                updated.getUsername(),
                updated.getRoles().stream().map(RoleEntity::getName).toList()
        );
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        admin.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    // === Roles endpoints ===
    @GetMapping("/roles")
    public List<String> listRoles() {
        return admin.findAllRoles()
                .stream()
                .map(RoleEntity::getName)
                .toList();
    }

    @PostMapping("/roles")
    public ResponseEntity<String> createRole(@RequestBody RoleRequest req) {
        RoleEntity r = admin.createRole(req.name());
        return ResponseEntity.status(201).body(r.getName());
    }

    @DeleteMapping("/roles/{name}")
    public ResponseEntity<Void> deleteRole(@PathVariable String name) {
        admin.deleteRole(name);
        return ResponseEntity.noContent().build();
    }
}
