package com.tanre.document_register.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClaimDocumentUpdateRequest {
    private String contractNumber;
    private String claimNumber;
    private Integer underwritingYear;
    private Long documentId;

    public ClaimDocumentUpdateRequest() {}

    public ClaimDocumentUpdateRequest(String contractNumber, String claimNumber, Integer underwritingYear, Long documentId) {
        this.contractNumber = contractNumber;
        this.claimNumber = claimNumber;
        this.underwritingYear = underwritingYear;
        this.documentId = documentId;
    }

}
