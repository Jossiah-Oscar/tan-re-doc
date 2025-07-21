package com.tanre.document_register.controller;

import com.tanre.document_register.dto.*;
import com.tanre.document_register.model.ClaimDocumentFile;
import com.tanre.document_register.model.ClaimDocumentFinanceStatus;
import com.tanre.document_register.model.ClaimDocuments;
import com.tanre.document_register.model.DocumentFile;
import com.tanre.document_register.repository.ClaimDocumentFileRepository;
import com.tanre.document_register.service.ClaimDocumentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/claim-documents")
@CrossOrigin(origins = "*")
public class ClaimDocumentController {

    private final ClaimDocumentService claimDocumentService;
    private final ClaimDocumentFileRepository  claimDocumentFileRepository;

    public ClaimDocumentController(ClaimDocumentService claimDocumentService, ClaimDocumentFileRepository claimDocumentFileRepository) {
        this.claimDocumentService = claimDocumentService;
        this.claimDocumentFileRepository = claimDocumentFileRepository;
    }


    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam String brokerCedant,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam String insuredName,
            @RequestParam String contractNumber,
            @RequestParam String claimNumber,
            @RequestParam LocalDate lossDate,
            @RequestParam int underwritingYear,
            @RequestParam int sequenceNumber
    ) {
        try {
            ClaimDocumentDTO claimDocumentDTO = new ClaimDocumentDTO();
            claimDocumentDTO.setBrokerCedant(brokerCedant);
            claimDocumentDTO.setInsuredName(insuredName);
            claimDocumentDTO.setContractNumber(contractNumber);
            claimDocumentDTO.setClaimNumber(claimNumber);
            claimDocumentDTO.setLossDate(lossDate);
            claimDocumentDTO.setUnderwritingYear(underwritingYear);
            claimDocumentDTO.setSequenceNumber(sequenceNumber);

            ClaimDocuments saved = claimDocumentService.createClaimDocument( claimDocumentDTO,files);
            return ResponseEntity.ok(saved);

        }catch (IllegalArgumentException e) {
            // Handles duplicate claim or other validation errors
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Validation Error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);

        }
        catch (MaxUploadSizeExceededException e) {
            return ResponseEntity.status(413).body("File too large! Maximum allowed size is 50MB.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing upload.");
        }
    }

    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Long fileId) {
        ClaimDocumentFile df = claimDocumentFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        ByteArrayResource resource = new ByteArrayResource(df.getData());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(df.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + df.getFileName() + "\"")
                .body(resource);
    }

    @PostMapping("/{docId}/upload")
    public ResponseEntity<?> uploadClaimDocument(
            @RequestParam("files") List<MultipartFile> files,
            @PathVariable Long docId
            ) throws IOException {

        claimDocumentService.uploadClaimDocument(docId, files);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/claim-payments")
    public List<ClaimDocuments> list(
    ) {
        return claimDocumentService.pendingClaimPayment()
                .stream()
                .collect(Collectors.toList());
    }

    @GetMapping("/claim-process-payments")
    public List<ClaimFinanceDocStatus> claimDocumentsListWithFinanceStatus(
    ) {
        return claimDocumentService.claimDocumentsListWithFinanceStatus()
                .stream()
                .collect(Collectors.toList());
    }


    @PostMapping("/{docId}/finance-status")
    public ResponseEntity<?> changeFinanceStatus(
            @PathVariable Long docId,
            @RequestBody ChangeFinanceStatusRequest request
    ) {
        claimDocumentService.changeFinanceStatus(docId, request.getComment(), request.getFinanceStatusId(), request.getMainStatusId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{docId}/status")
    public ResponseEntity<?> processDocument(
            @PathVariable Long docId,
            @RequestBody ProcessDocumentRequest request
    ) {
        claimDocumentService.processDocument(docId, request.getStatusId());
        return ResponseEntity.ok().build();  // You can return a DTO if needed
    }

    @GetMapping("/finance-status")
    public List<ClaimDocumentFinanceStatus> getFinanceStatus(
    ) {
        return claimDocumentService.getClaimDocFinanceStatus()
                .stream()
                .collect(Collectors.toList());
    }

    @GetMapping("/{docId}/files")
    public List<ClaimDocFileDTO> getClaimDocuments(
            @PathVariable Long docId
    ){
        return claimDocumentService.getAllClaimDocFiles(docId);
    }

}
