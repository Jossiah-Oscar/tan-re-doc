package com.tanre.document_register.service;


import com.tanre.document_register.dto.DocumentDTO;
import com.tanre.document_register.dto.DocumentDetailsDTO;
import com.tanre.document_register.dto.DocumentFileDTO;
import com.tanre.document_register.dto.DocumentTransactionDTO;
import com.tanre.document_register.model.*;
import com.tanre.document_register.repository.BrokerCedantRepository;
import com.tanre.document_register.repository.DocumentFileRepository;
import com.tanre.document_register.repository.DocumentRepository;
import com.tanre.document_register.repository.DocumentTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    private final DocumentRepository docRepo;
    private final DocumentFileRepository fileRepo;
    private final BrokerCedantRepository brokerCedantRepository;
    private final DocumentTransactionRepository documentTransactionRepository;
    private final EmailService emailService;

    public DocumentService(DocumentRepository docRepo,
                           BrokerCedantRepository brokerCedantRepository,
                           DocumentFileRepository fileRepo,
                           EmailService emailService,  DocumentTransactionRepository documentTransactionRepository) {
        this.docRepo = docRepo;
        this.fileRepo = fileRepo;
        this.emailService = emailService;
        this.documentTransactionRepository = documentTransactionRepository;
        this.brokerCedantRepository = brokerCedantRepository;
    }

    @Transactional
    public Document createDocument(String cedantName,
                                   String documentType,
                                   String fileName,
                                   List<MultipartFile> files) throws IOException {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Document doc = new Document();
        doc.setCedantName(cedantName);
        doc.setDocumentType(documentType);
        doc.setFileName(fileName);
        doc.setStatus(Status.PENDING);
        doc.setCreatedBy(username);
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
//        emailService.sendDocumentSubmissionEmail(saved);
        return saved;
    }

    public List<DocumentDTO> search(String cedantName, String documentType) {


        List<Document> documents = docRepo.findByCedantNameContainingIgnoreCaseAndDocumentTypeContainingIgnoreCase(
                cedantName == null ? "" : cedantName,
                documentType == null ? "" : documentType);

        List<DocumentDTO> documentDTOS = new ArrayList<>();;

        for (Document doc : documents) {
            DocumentDTO dto = new DocumentDTO();

            dto.setId(doc.getId());
            dto.setCedantName(brokerCedantRepository.findNameByCode(doc.getCedantName()));
            dto.setDocumentType(doc.getDocumentType());
            dto.setFileName(doc.getFileName());
            dto.setCreatedBy(doc.getCreatedBy());
            dto.setStatus(doc.getStatus());
            dto.setDateCreated(doc.getDateCreated());
            dto.setDateUpdated(doc.getDateUpdated());

            documentDTOS.add(dto);
        }

        return documentDTOS;
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
        dto.setCreatedBy(doc.getCreatedBy());
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

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();


        Document doc = docRepo
                .findByIdAndCreatedBy(id, username)
                .orElseThrow(() ->
                        new AccessDeniedException("Cannot delete a document you did not create"));

        docRepo.delete(doc);
        // with cascade = ALL + orphanRemoval = true on files & evidence,
        // child rows will be cleaned up automatically.
    }


    /** Finance only */
    @PreAuthorize("hasRole('FINANCE')")
    @Transactional
    public void reverseDocument(Long docId, String comment) {
        Document doc = docRepo.findById(docId)
                .orElseThrow(() -> new EntityNotFoundException("Doc not found"));
        Status old = doc.getStatus();
        doc.setStatus(Status.RETURNED);
        docRepo.save(doc);

        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        DocumentTransaction documentTransaction = new DocumentTransaction();
        documentTransaction.setDocument(doc);
        documentTransaction.setOldStatus(old);
        documentTransaction.setNewStatus(Status.RETURNED);
        documentTransaction.setComment(comment);
        documentTransaction.setChangedBy(user);
        documentTransactionRepository.save(documentTransaction);
    }

    /** Uploader only, and only when PENDING */
    @PreAuthorize("hasRole('OPERATION')")
    @Transactional
    public Document updateDocument(Long docId,
                                   String cedantName,
                                   String documentType,
                                   String fileName) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        Document doc = docRepo.findByIdAndCreatedBy(docId, user)
                .orElseThrow(() ->
                        new AccessDeniedException("Can only edit your own pending docs"));
        if (doc.getStatus() != Status.PENDING) {
            throw new IllegalStateException("Can only edit when Pending");
        }
        doc.setCedantName(cedantName);
        doc.setDocumentType(documentType);
        doc.setFileName(fileName);
        return docRepo.save(doc);
    }

    @Transactional
    public void addFilesToDocument(Long docId, List<MultipartFile> files) throws IOException {
        Document doc = docRepo.findById(docId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        Instant now = Instant.now();

        for (MultipartFile mf : files) {
            DocumentFile df = new DocumentFile();
            df.setDocument(doc);
            df.setFileName(mf.getOriginalFilename());
            df.setContentType(mf.getContentType());
            df.setData(mf.getBytes());
            fileRepo.save(df);
        }

        // record a transaction
        DocumentTransaction tx = new DocumentTransaction();
        tx.setDocument(doc);
        tx.setOldStatus(doc.getStatus());        // or null if not status-related
        tx.setNewStatus(doc.getStatus());
        tx.setComment("Uploaded " + files.size() + " new file(s)");
        tx.setChangedBy(user);
        tx.setChangedAt(now);
        documentTransactionRepository.save(tx);
    }

    @Transactional()
    public List<DocumentTransactionDTO> getTransactionsForDocument(Long docId) {
        return documentTransactionRepository.findByDocumentIdOrderByChangedAtDesc(docId).stream()
                .map(tx -> new DocumentTransactionDTO(
                        tx.getId(),
                        tx.getOldStatus().name(),
                        tx.getNewStatus().name(),
                        tx.getComment(),
                        tx.getChangedBy(),
                        tx.getChangedAt()
                ))
                .toList();
    }
}