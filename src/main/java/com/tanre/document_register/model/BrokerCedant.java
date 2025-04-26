package com.tanre.document_register.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "broker_cedant")
@Getter
@Setter
public class BrokerCedant {
    @Id
    @Column(name = "broker_cedant_code", length = 10)
    private String code;

    @Column(name = "broker_cedant_name", length = 255, nullable = false)
    private String name;

    @Column(name = "broker_cedant_type", length = 1, nullable = false)
    private String type;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}