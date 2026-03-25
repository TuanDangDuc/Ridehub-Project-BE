package com.tuan.ridehub.repository;

import com.tuan.ridehub.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {

    @Query("select a from Users a where a.username = ?1 or a.email = ?2")
    Users findUsersByUsername(String username, String email);

    @Query("select a from Users a where a.username = ?1")
    Users loadUserByUsername(String username);
}
