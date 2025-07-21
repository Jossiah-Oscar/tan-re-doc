package com.tanre.document_register.dto;


import com.tanre.document_register.model.Status;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;


@Getter
@Setter
public class ClaimDocumentDTO {
    private Long id;
    private String brokerCedant;
    private String insuredName;
    private String contractNumber;
    private String claimNumber;
    private LocalDate lossDate;
    int underwritingYear;
    private int sequenceNumber;

}
