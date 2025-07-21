package com.tanre.document_register.repository;

import com.tanre.document_register.model.ClaimDocumentStatus;
import com.tanre.document_register.model.ClaimDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ClaimDocumentStatusRepository extends JpaRepository<ClaimDocumentStatus, Long> {

    Optional<ClaimDocumentStatus> findByName(String name);

    List<ClaimDocumentStatus> findClaimDocumentStatusById(Long id);
}
