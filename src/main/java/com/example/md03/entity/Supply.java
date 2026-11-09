package com.example.md03.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "supplies")
@Getter @Setter @NoArgsConstructor
public class Supply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String specification;
    private String provider;
    private String unit;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}