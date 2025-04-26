package com.tanre.document_register.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_files")
@Setter
@Getter
public class DocumentFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. Content type
    private String contentType;

    // 2. Actual blob data
    @Lob
    @Column(name = "file_data")
    @JsonIgnore               // ← tell Jackson to skip this field
    private byte[] data;

    // 3. Timestamp
    @CreationTimestamp
    private LocalDateTime dateUploaded;

    // 4. FK to Document
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    // 5. File name (moved to last)
    private String fileName;

}
