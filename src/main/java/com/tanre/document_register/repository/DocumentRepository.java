package com.tanre.document_register.repository;

import com.tanre.document_register.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document>
    findByCedantNameContainingIgnoreCaseAndDocumentTypeContainingIgnoreCase(
            String cedantName, String documentType);

    Optional<Document> findByIdAndCreatedBy(Long id, String createdBy);
}