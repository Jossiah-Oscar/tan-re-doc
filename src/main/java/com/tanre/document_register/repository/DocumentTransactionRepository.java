package com.tanre.document_register.repository;


import com.tanre.document_register.model.DocumentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentTransactionRepository
        extends JpaRepository<DocumentTransaction,Long> {
    List<DocumentTransaction> findByDocumentIdOrderByChangedAtDesc(Long documentId);

}