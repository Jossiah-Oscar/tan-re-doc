package com.tanre.document_register.repository;

import com.tanre.document_register.model.ClaimDocTransaction;
import com.tanre.document_register.model.ClaimDocumentFinanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimDocFinanceStatusRepo extends JpaRepository<ClaimDocumentFinanceStatus, Long> {
}
