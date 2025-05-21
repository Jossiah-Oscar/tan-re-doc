package com.tanre.document_register.service;

import com.tanre.document_register.dto.EvidenceDTO;
import com.tanre.document_register.model.Document;
import com.tanre.document_register.model.DocumentTransaction;
import com.tanre.document_register.model.Evidence;
import com.tanre.document_register.model.Status;
import com.tanre.document_register.repository.DocumentRepository;
import com.tanre.document_register.repository.DocumentTransactionRepository;
import com.tanre.document_register.repository.EvidenceRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvidenceService {
    private final EvidenceRepository evidenceRepo;
    private final DocumentRepository docRepo;
    private final DocumentTransactionRepository documentTransactionRepository;

    public EvidenceService(EvidenceRepository evidenceRepo, DocumentRepository docRepo, DocumentTransactionRepository documentTransactionRepository) {
        this.evidenceRepo = evidenceRepo;
        this.docRepo = docRepo;
        this.documentTransactionRepository = documentTransactionRepository;
    }

    public Evidence addEvidence(Long documentId, MultipartFile file) throws IOException {
        Document doc = docRepo.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        Evidence e = new Evidence();
        e.setDocument(doc);
        e.setFileName(file.getOriginalFilename());
        e.setContentType(file.getContentType());
        e.setData(file.getBytes());
        Evidence saved = evidenceRepo.save(e);

        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        DocumentTransaction documentTransaction = new DocumentTransaction();
        documentTransaction.setDocument(doc);
        documentTransaction.setOldStatus(doc.getStatus());
        documentTransaction.setNewStatus(Status.DONE);
        documentTransaction.setComment("Evidence added");
        documentTransaction.setChangedBy(user);
        documentTransactionRepository.save(documentTransaction);

        doc.setStatus(Status.DONE);
        docRepo.save(doc);
        return saved;
    }

    public List<EvidenceDTO> listEvidence(Long documentId) {
        List<Evidence> evidences = evidenceRepo.findByDocumentIdOrderByDateUploadedDesc(documentId);
        return evidences.stream()
                .map(e -> new EvidenceDTO(
                        e.getId(),
                        e.getFileName(),
                        e.getContentType(),
                        e.getDateUploaded()))
                .collect(Collectors.toList());
    }

    public Resource loadEvidence(Long evidenceId) throws IOException {
        Evidence ev = evidenceRepo.findById(evidenceId)
                .orElseThrow(() -> new FileNotFoundException("Evidence not found: " + evidenceId));

        // if you’re using blob storage
        return new ByteArrayResource(ev.getData());
    }
}