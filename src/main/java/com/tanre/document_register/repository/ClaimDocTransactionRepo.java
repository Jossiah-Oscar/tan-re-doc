package com.tanre.document_register.repository;

import com.tanre.document_register.model.ClaimDocTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ClaimDocTransactionRepo extends JpaRepository<ClaimDocTransaction, Long> {

    Optional<ClaimDocTransaction> findTopByDocumentIdOrderByChangedAtDesc(Long documentId);
}
