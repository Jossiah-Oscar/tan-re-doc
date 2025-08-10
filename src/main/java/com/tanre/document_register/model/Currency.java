package com.tanre.document_register.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "currency")
@Setter
@Getter
public class Currency {
    @Id
    @Column(length = 10)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "exchange_rate", precision = 18, scale = 8, nullable = false)
    private BigDecimal exchangeRate;

    // getters & setters
}
