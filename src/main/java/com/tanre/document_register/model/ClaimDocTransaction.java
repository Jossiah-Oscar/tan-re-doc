package com.tanre.document_register.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "claim_doc_transaction")
public class ClaimDocTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ClaimDocuments document;

    @ManyToOne
    private ClaimDocumentStatus fromStatus;

    @ManyToOne
    private ClaimDocumentStatus toStatus;

    @ManyToOne
    @JoinColumn(nullable = true)
    private ClaimDocumentFinanceStatus fromFinanceStatus;

    @ManyToOne
    private ClaimDocumentFinanceStatus toFinanceStatus;

    private String comment;

    @Column(name = "changed_by", nullable = false)
    private String changedBy;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private Instant changedAt = Instant.now();
}
