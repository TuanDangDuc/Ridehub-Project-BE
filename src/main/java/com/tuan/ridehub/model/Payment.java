package com.tuan.ridehub.model;

import com.tuan.ridehub.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Double amount;
    private String paymentMethod;
    private PaymentStatus paymentStatus;
    @Column(columnDefinition = "TEXT")
    private String paymentUrl;
    @Column(columnDefinition = "TEXT")
    private String responseData;

    @ManyToOne
    private Users user;

    @OneToMany(
            mappedBy = "payment",
            cascade = CascadeType.MERGE
    )
    private List<Trip> trips;
}
