package com.tanre.document_register.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClaimDocFileDTO {
    private Long id;
    private String fileName;
    private String contentType;
    private LocalDateTime dateUploaded;
}
