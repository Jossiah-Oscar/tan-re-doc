package com.tanre.document_register.service;

import com.tanre.document_register.model.Document;
import com.tanre.document_register.model.Evidence;
import com.tanre.document_register.model.Status;
import com.tanre.document_register.repository.DocumentRepository;
import com.tanre.document_register.repository.EvidenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class EvidenceService {
    private final EvidenceRepository evidenceRepo;
    private final DocumentRepository docRepo;

    public EvidenceService(EvidenceRepository evidenceRepo, DocumentRepository docRepo) {
        this.evidenceRepo = evidenceRepo;
        this.docRepo = docRepo;
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

        doc.setStatus(Status.DONE);
        docRepo.save(doc);
        return saved;
    }
}