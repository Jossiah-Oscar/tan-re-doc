package com.tanre.document_register.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "evidence")
@Setter
@Getter
public class Evidence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contentType;

    @Lob
//    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "file_data")
    private byte[] data;

    @CreationTimestamp
    private LocalDateTime dateUploaded;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    private String fileName;

    // … getters & setters …
}

