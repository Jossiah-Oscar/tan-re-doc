package com.tanre.document_register.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim_documents")
@Setter
@Getter
public class ClaimDocuments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String brokerCedant;
    private String insured;
    private String contractNumber;
    private String claimNumber;
    private LocalDate lossDate;
    private int underwritingYear;
    private int sequenceNo;
    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private ClaimDocumentStatus status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
