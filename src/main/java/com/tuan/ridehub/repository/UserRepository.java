package com.tuan.ridehub.repository;

import com.tuan.ridehub.dto.response.UserDtopResponse;
import com.tuan.ridehub.enums.AccountStatus;
import com.tuan.ridehub.enums.Role;
import com.tuan.ridehub.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {

    @Query("select a from Users a where a.username = ?1 or a.email = ?2")
    Users findUsersByUsername(String username, String email);

    @Query("select a from Users a where a.username = ?1")
    Users findUsersByUsername(String username);

    @Query("select a from Users a where a.username = ?1")
    Users loadUserByUsername(String username);

    @Modifying
    @Transactional
    @Query("update Users a set " +
            "a.firstname = :#{#user.firstname}, " +
            "a.lastname = :#{#user.lastname}, " +
            "a.sex = :#{#user.sex}," +
            "a.dateOfBirth = :#{#user.dateOfBirth}, " +
            "a.identityNumber = :#{#user.identityNumber}, " +
            "a.avatarUrl = :#{#user.avatarUrl}, " +
            "a.phoneNumber = :#{#user.phoneNumber} " +
            "where a.id = :#{#user.id}")
    void updateUserInfo(Users user);

    @Modifying
    @Transactional
    @Query("update Users a set a.status = ?2 where a.id = ?1")
    void setStatus(UUID id, AccountStatus status);


    @Modifying
    @Transactional
    @Query("update Users a set a.role = ?2 where a.id = ?1")
    void setRole(UUID id, Role role);


    @Modifying
    @Transactional
    @Query("update Users a set a.password = ?2 where a.id = ?1")
    void changePassword(Users users);

    @Query("select a from Users a")
    List<Users> getAllUser();

    @Query("select a from Users a where a.id = ?1")
    UserDtopResponse findFirstById(UUID id);
}
