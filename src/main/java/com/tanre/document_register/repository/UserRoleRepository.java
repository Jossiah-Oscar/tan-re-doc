package com.tanre.document_register.repository;

import com.tanre.document_register.model.RoleEntity;
import com.tanre.document_register.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<RoleEntity, Long> {
    @Query("""
    select r.name 
      from RoleEntity r 
      join r.users u 
     where u.username = :username
  """)
    List<String> findRoleNamesByUsername(@Param("username") String username);

    Optional<RoleEntity> findByName(String name);
}
