package com.tanre.document_register.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
public class DocumentFileDTO {
    private Long id;
    private String fileName;
    private String contentType;
    private LocalDateTime dateUploaded;

    // constructors
    public DocumentFileDTO() {}
    public DocumentFileDTO(Long id, String fileName, String contentType, LocalDateTime dateUploaded) {
        this.id = id;
        this.fileName = fileName;
        this.contentType = contentType;
        this.dateUploaded = dateUploaded;
    }

    // getters & setters omitted for brevity
}