package com.tuan.ridehub.model;


import com.tuan.ridehub.enums.Type;
import com.tuan.ridehub.enums.VehicleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(columnDefinition = "TEXT")
    private String name;
    private String code;
    @Enumerated(EnumType.STRING)
    private VehicleStatus status;
    @Enumerated(EnumType.STRING)
    private Type type;

    @OneToMany(
            mappedBy = "vehicle",
            cascade = CascadeType.MERGE
    )
    private List<Trip> trips;

    @ManyToOne()
    private Pricing pricing;
}
