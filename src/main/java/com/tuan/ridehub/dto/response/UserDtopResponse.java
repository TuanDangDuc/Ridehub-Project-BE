package com.tuan.ridehub.dto.response;

import com.tuan.ridehub.enums.AccountStatus;
import com.tuan.ridehub.enums.Role;
import com.tuan.ridehub.enums.Sex;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserDtopResponse {
    private UUID id;
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    @Enumerated(EnumType.STRING)
    private Sex sex;
    private LocalDateTime dateOfBirth;
    private String identityNumber;
    @Column(columnDefinition = "TEXT")
    private String avatarUrl;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
