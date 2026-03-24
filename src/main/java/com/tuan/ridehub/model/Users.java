package com.tuan.ridehub.model;


import com.tuan.ridehub.enums.AccountStatus;
import com.tuan.ridehub.enums.Role;
import com.tuan.ridehub.enums.Sex;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Users extends Account{
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
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Payment> payments;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Trip> trips;
}
