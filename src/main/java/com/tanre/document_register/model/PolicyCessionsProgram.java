package com.tanre.document_register.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "policy_cessions_program")
@Setter
@Getter
public class PolicyCessionsProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "business_class_id", nullable = false)
    private BusinessClass businessClass;

    @ManyToOne
    @JoinColumn(name = "contract_type_id", nullable = false)
    private ContractType contractType;

    private Long retention;
    private Integer lines;
    private Long treatyLimit;
    private Long totalCapacity;
    private String rmsCode;

    @ManyToOne
    @JoinColumn(name = "currency_code", nullable = false)
    private Currency currency;

    private Integer year;

    // getters & setters
}