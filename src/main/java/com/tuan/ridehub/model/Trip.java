package com.tuan.ridehub.model;

import com.tuan.ridehub.enums.TripStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private Double distance;
    private Double totalCost;
    @Enumerated(EnumType.STRING)
    private TripStatus tripStatus;
    @Column(precision = 10, scale = 6)
    private BigDecimal latitude;
    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;

    @ManyToOne
    private Users user;

    @ManyToOne
    private Payment payment;

    @ManyToOne
    private Pricing pricing;

    @ManyToOne
    private Vehicle vehicle;

    @ManyToOne
    private Station startStation;

    @ManyToOne
    private Station endStation;
}

