package com.tanre.document_register.repository;

import com.tanre.document_register.model.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    List<Evidence> findByDocumentIdOrderByDateUploadedDesc(Long documentId);

}