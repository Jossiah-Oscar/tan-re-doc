package com.tanre.document_register.repository;


import com.tanre.document_register.dto.ClaimDocumentUpdateRequest;
import com.tanre.document_register.model.ClaimDocumentStatus;
import com.tanre.document_register.model.ClaimDocuments;
import com.tanre.document_register.model.Document;
import com.tanre.document_register.model.DocumentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Modifying
    @Query("UPDATE ClaimDocuments d SET d.status = :status WHERE d.id IN :documentIds")
    void updateStatusBatch(@Param("documentIds") List<Long> documentIds,
                           @Param("status") ClaimDocumentStatus status);

}
