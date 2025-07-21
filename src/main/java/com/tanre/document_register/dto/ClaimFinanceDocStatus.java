package com.tanre.document_register.dto;

import com.tanre.document_register.model.ClaimDocumentFinanceStatus;
import com.tanre.document_register.model.ClaimDocumentStatus;
import com.tanre.document_register.model.ClaimDocuments;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClaimFinanceDocStatus {
   private ClaimDocuments claimDocuments;
   private ClaimDocumentFinanceStatus claimDocumentFinanceStatus;
}
