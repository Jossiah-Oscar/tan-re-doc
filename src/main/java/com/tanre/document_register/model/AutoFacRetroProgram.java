package com.tanre.document_register.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "auto_fac_retro_program")
@Getter
@Setter
public class AutoFacRetroProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "business_class_id", nullable = false)
    private BusinessClass businessClass;

    private Long tzsCapacity;
    private Long usdCapacity;
    private Integer year;

    // getters & setters
}
