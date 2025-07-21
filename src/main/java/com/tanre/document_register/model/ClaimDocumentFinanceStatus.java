package com.tanre.document_register.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ClaimDocumentFinanceStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String label;

    @Column
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "main_status_id")
    private ClaimDocumentStatus mainStatus;

    @Column(nullable = false)
    private boolean requiresComment = false;

    private boolean active = true;

}
