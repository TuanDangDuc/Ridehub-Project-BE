package com.tuan.ridehub.dto.request;

import com.tuan.ridehub.enums.AccountStatus;
import com.tuan.ridehub.enums.Role;
import com.tuan.ridehub.enums.Sex;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class UpdateUserDtoRequest {
    UUID id;
    private String firstname;
    private String lastname;
    @Enumerated(EnumType.STRING)
    private Sex sex;
    private LocalDateTime dateOfBirth;
    private String identityNumber;
    @Column(columnDefinition = "TEXT")
    private String avatarUrl;
    private String phoneNumber;
}
