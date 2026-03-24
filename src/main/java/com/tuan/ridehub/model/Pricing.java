package com.tuan.ridehub.model;


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
public class Pricing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Double pricePerMinutes;

    @OneToMany(
            mappedBy = "pricing",
            cascade = CascadeType.MERGE
    )
    private List<Trip> trips;
}
