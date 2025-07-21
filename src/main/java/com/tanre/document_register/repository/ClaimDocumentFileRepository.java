package com.tanre.document_register.repository;


import com.tanre.document_register.model.ClaimDocumentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimDocumentFileRepository extends JpaRepository<ClaimDocumentFile, Long> {
    List<ClaimDocumentFile> findAllByClaimDocuments_Id(Long docId);
}
