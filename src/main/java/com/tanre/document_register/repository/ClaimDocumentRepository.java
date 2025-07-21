package com.tanre.document_register.repository;


import com.tanre.document_register.model.ClaimDocuments;
import com.tanre.document_register.model.Document;
import com.tanre.document_register.model.DocumentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimDocumentRepository extends JpaRepository<ClaimDocuments, Long> {

    List<ClaimDocuments>
    findAll();

    Optional<ClaimDocuments> findByClaimNumber(String claimNumber);

    List<ClaimDocuments> findByStatus_Id(Long statusId);

    List<ClaimDocuments> findByStatus_IdNot(Long statusId);
}
