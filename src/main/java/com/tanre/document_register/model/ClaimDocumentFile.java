package com.tanre.document_register.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.type.descriptor.jdbc.VarbinaryJdbcType;

import java.time.LocalDateTime;
@Entity
@Table(name = "claim_document_upload_test")
@Setter
@Getter
public class ClaimDocumentFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contentType;

    @Lob
    @JdbcType(VarbinaryJdbcType.class)
    @Column(name = "file_bytes", columnDefinition = "BYTEA", nullable = false)
    @JsonIgnore
    private byte[] data;

    @CreationTimestamp
    private LocalDateTime dateUploaded;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_doc_id", nullable = false)
    private ClaimDocuments claimDocuments;

    private String fileName;
}
