package com.tanre.document_register.dto;

import com.tanre.document_register.model.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class DocumentDetailsDTO {
    private Long id;
    private String cedantName;
    private String documentType;
    private String fileName;
    private Status status;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;

    private List<DocumentFileDTO> files;

    // getters & setters omitted for brevity
}