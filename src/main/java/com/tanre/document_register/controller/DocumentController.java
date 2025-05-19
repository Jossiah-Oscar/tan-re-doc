package com.tanre.document_register.controller;

import com.tanre.document_register.dto.DocumentDTO;
import com.tanre.document_register.dto.DocumentDetailsDTO;
import com.tanre.document_register.dto.DocumentFileDTO;
import com.tanre.document_register.dto.EvidenceDTO;
import com.tanre.document_register.model.Document;
import com.tanre.document_register.model.DocumentFile;
import com.tanre.document_register.model.DocumentTransaction;
import com.tanre.document_register.model.Evidence;
import com.tanre.document_register.repository.DocumentFileRepository;
import com.tanre.document_register.repository.DocumentTransactionRepository;
import com.tanre.document_register.service.DocumentService;
import com.tanre.document_register.service.EvidenceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {
    private final DocumentService docService;
    private final EvidenceService evidenceService;
    private final DocumentFileRepository fileRepo;
    private final DocumentTransactionRepository documentTransactionRepository;


    public DocumentController(DocumentService docService,
                              EvidenceService evidenceService,
                              DocumentFileRepository fileRepo,DocumentTransactionRepository documentTransactionRepository ) {
        this.docService = docService;
        this.evidenceService = evidenceService;
        this.fileRepo = fileRepo;
        this.documentTransactionRepository = documentTransactionRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam String cedantCode,
            @RequestParam String documentType,
            @RequestParam String fileName,
            @RequestParam("files") List<MultipartFile> files
    ) {
        try {
            Document saved = docService.createDocument(cedantCode, documentType, fileName, files);
            return ResponseEntity.ok(saved);
        } catch (MaxUploadSizeExceededException e) {
            return ResponseEntity.status(413).body("File too large! Maximum allowed size is 50MB.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing upload.");
        }
    }

    @GetMapping
    public List<DocumentDTO> list(
            @RequestParam(required = false) String cedantName,
            @RequestParam(required = false) String documentType
    ) {
        return docService.search(cedantName, documentType)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private DocumentDTO toDto(Document doc) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(doc.getId());
        dto.setCedantName(doc.getCedantName());
        dto.setDocumentType(doc.getDocumentType());
        dto.setFileName(doc.getFileName());
        dto.setStatus(doc.getStatus());
        dto.setCreatedBy(doc.getCreatedBy());
        dto.setDateCreated(doc.getDateCreated());
        dto.setDateUpdated(doc.getDateUpdated());
        return dto;
    }

    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Long fileId) {
        DocumentFile df = fileRepo.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        ByteArrayResource resource = new ByteArrayResource(df.getData());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(df.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + df.getFileName() + "\"")
                .body(resource);
    }

    @PostMapping("/{id}/evidence")
    public ResponseEntity<Evidence> addEvidence(
            @PathVariable Long id,
            @RequestParam("evidence") MultipartFile evidenceFile
    ) throws IOException {
        Evidence saved = evidenceService.addEvidence(id, evidenceFile);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDetailsDTO> getDocumentDetails(@PathVariable Long id) {
        DocumentDetailsDTO details = docService.getDocumentDetails(id);
        return ResponseEntity.ok(details);
    }




    @GetMapping("/{id}/files")
    public ResponseEntity<List<DocumentFileDTO>> listFiles(@PathVariable Long id) {
        List<DocumentFileDTO> files = docService.getDocumentsByDocId(id);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        try {
            docService.deleteDocument(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/reverse")
    public ResponseEntity<Void> reverse(
            @PathVariable Long id,
            @RequestBody Map<String,String> body) {
        docService.reverseDocument(id, body.get("comment"));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> update(
            @PathVariable Long id,
            @RequestBody Map<String,String> body) {
        Document updated = docService.updateDocument(
                id,
                body.get("cedantName"),
                body.get("documentType"),
                body.get("fileName")
        );
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<DocumentTransaction>> transactions(@PathVariable Long id) {
        return ResponseEntity.ok(
                documentTransactionRepository.findByDocumentIdOrderByChangedAtDesc(id)
        );
    }

    @GetMapping("/{docId}/evidence/{evidenceId}/download")
    public ResponseEntity<Resource> downloadEvidence(
            @PathVariable Long evidenceId
    ) throws IOException {
        Resource resource = evidenceService.loadEvidence(evidenceId);
        String filename = "evidence-" + evidenceId;
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @GetMapping("/{id}/evidence")
    public ResponseEntity<List<EvidenceDTO>> listEvidence(@PathVariable("id") Long documentId) {
        List<EvidenceDTO> dto = evidenceService.listEvidence(documentId);
        return ResponseEntity.ok(dto);
    }

}