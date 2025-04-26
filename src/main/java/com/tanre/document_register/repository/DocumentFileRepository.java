package com.tanre.document_register.repository;

import com.tanre.document_register.model.DocumentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DocumentFileRepository extends JpaRepository<DocumentFile, Long> {
    List<DocumentFile> findByDocumentIdOrderByDateUploadedDesc(Long documentId);

}
