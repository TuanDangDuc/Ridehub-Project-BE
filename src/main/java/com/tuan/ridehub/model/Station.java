package com.tuan.ridehub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(columnDefinition = "TEXT")
    private String name;
    @Column(precision = 10, scale = 6)
    private BigDecimal latitude;
    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;
    private Integer capacity;
    private Integer currentVehicleCount;

    @OneToMany(
            mappedBy = "startStation",
            cascade = CascadeType.MERGE
    )
    private List<Trip> trip1;

    @OneToMany(
            mappedBy = "endStation",
            cascade = CascadeType.MERGE
    )
    private List<Trip> trip2;
}
