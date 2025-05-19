package com.tanre.document_register.service;

import com.tanre.document_register.model.RoleEntity;
import com.tanre.document_register.model.UserEntity;
import com.tanre.document_register.repository.UserRepository;
import com.tanre.document_register.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminUserService {
    private final UserRepository userRepo;
    private final UserRoleRepository roleRepo;

    public AdminUserService(UserRepository userRepo, UserRoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    @Transactional
    public List<UserEntity> findAllUsers() {
        return userRepo.findAll();
    }

    @Transactional
    public UserEntity createUser(String username, List<String> roleNames) {
        if (userRepo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("User already exists: " + username);
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        Set<RoleEntity> roles = roleNames.stream()
                .map(this::findOrCreateRole)
                .collect(Collectors.toSet());
        user.setRoles(roles);
        return userRepo.save(user);
    }

    @Transactional
    public UserEntity replaceUserRoles(String username, List<String> roleNames) {
        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Unknown user: " + username));
        Set<RoleEntity> roles = roleNames.stream()
                .map(this::findOrCreateRole)
                .collect(Collectors.toSet());
        user.setRoles(roles);
        return userRepo.save(user);
    }

    @Transactional
    public void deleteUser(String username) {
        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Unknown user: " + username));
        userRepo.delete(user);
    }

    @Transactional
    public List<RoleEntity> findAllRoles() {
        return roleRepo.findAll();
    }

    @Transactional
    public RoleEntity createRole(String roleName) {
        return findOrCreateRole(roleName);
    }

    /** Delete a role (for DELETE /admin/roles/{name}) */
    @Transactional
    public void deleteRole(String roleName) {
        RoleEntity role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + roleName));
        roleRepo.delete(role);
    }

    private RoleEntity findOrCreateRole(String roleName) {
        return roleRepo.findByName(roleName)
                .orElseGet(() -> {
                    RoleEntity r = new RoleEntity();
                    r.setName(roleName);
                    return roleRepo.save(r);
                });
    }

    @Transactional
    public UserEntity findOrCreateUser(String username, List<String> defaultRoles) {
        return userRepo.findByUsername(username).orElseGet(() -> {
                    UserEntity u = new UserEntity();
                    u.setUsername(username);
                    Set<RoleEntity> roles = defaultRoles.stream()
                            .map(roleName -> roleRepo.findByName(roleName)
                                    .orElseGet(() -> roleRepo.save(new RoleEntity(null, roleName, Set.of()))))
                            .collect(Collectors.toSet());
                    u.setRoles(roles);
                    return userRepo.save(u);
                });
    }
}