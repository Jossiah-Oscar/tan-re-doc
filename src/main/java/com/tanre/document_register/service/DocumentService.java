package com.tanre.document_register.service;


import com.tanre.document_register.dto.DocumentDetailsDTO;
import com.tanre.document_register.dto.DocumentFileDTO;
import com.tanre.document_register.model.Document;
import com.tanre.document_register.model.DocumentFile;
import com.tanre.document_register.model.Status;
import com.tanre.document_register.repository.DocumentFileRepository;
import com.tanre.document_register.repository.DocumentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    private final DocumentRepository docRepo;
    private final DocumentFileRepository fileRepo;
    private final EmailService emailService;

    public DocumentService(DocumentRepository docRepo,
                           DocumentFileRepository fileRepo,
                           EmailService emailService) {
        this.docRepo = docRepo;
        this.fileRepo = fileRepo;
        this.emailService = emailService;
    }

    @Transactional
    public Document createDocument(String cedantName,
                                   String documentType,
                                   String fileName,
                                   List<MultipartFile> files) throws IOException {
        Document doc = new Document();
        doc.setCedantName(cedantName);
        doc.setDocumentType(documentType);
        doc.setFileName(fileName);
        doc.setStatus(Status.PENDING);
        Document saved = docRepo.save(doc);

        for (MultipartFile file : files) {
            DocumentFile df = new DocumentFile();
            df.setDocument(saved);
            df.setFileName(file.getOriginalFilename());
            df.setContentType(file.getContentType());
            df.setData(file.getBytes());
            fileRepo.save(df);
        }

        // *** SEND EMAIL ***
        emailService.sendDocumentSubmissionEmail(saved);
        return saved;
    }

    public List<Document> search(String cedantName, String documentType) {
        return docRepo.findByCedantNameContainingIgnoreCaseAndDocumentTypeContainingIgnoreCase(
                cedantName == null ? "" : cedantName,
                documentType == null ? "" : documentType);
    }

    public DocumentDetailsDTO getDocumentDetails(Long documentId) {
        Document doc = docRepo.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        List<DocumentFileDTO> fileDTOs = fileRepo
                .findByDocumentIdOrderByDateUploadedDesc(documentId)
                .stream()
                .map(f -> new DocumentFileDTO(
                        f.getId(),
                        f.getFileName(),
                        f.getContentType(),
                        f.getDateUploaded()))
                .toList();

        DocumentDetailsDTO dto = new DocumentDetailsDTO();
        dto.setId(doc.getId());
        dto.setCedantName(doc.getCedantName());
        dto.setDocumentType(doc.getDocumentType());
        dto.setFileName(doc.getFileName());
        dto.setStatus(doc.getStatus());
        dto.setDateCreated(doc.getDateCreated());
        dto.setDateUpdated(doc.getDateUpdated());
        dto.setFiles(fileDTOs);

        return dto;
    }

    public List<DocumentFileDTO> getDocumentsByDocId(Long employeeId) {
        return fileRepo.findByDocumentIdOrderByDateUploadedDesc(employeeId)
                .stream()
                .map(f -> new DocumentFileDTO(
                f.getId(),
                f.getFileName(),
                f.getContentType(),
                f.getDateUploaded()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteDocument(Long id) {
        if (!docRepo.existsById(id)) {
            throw new EntityNotFoundException("Document not found");
        }
        docRepo.deleteById(id);
        // with cascade = ALL + orphanRemoval = true on files & evidence,
        // child rows will be cleaned up automatically.
    }
}