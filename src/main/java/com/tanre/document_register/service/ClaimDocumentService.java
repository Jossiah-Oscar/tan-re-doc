package com.tanre.document_register.service;

import com.tanre.document_register.controller.ClaimDocumentController;
import com.tanre.document_register.dto.ClaimDocFileDTO;
import com.tanre.document_register.dto.ClaimDocumentDTO;
import com.tanre.document_register.dto.ClaimDocumentUpdateRequest;
import com.tanre.document_register.dto.ClaimFinanceDocStatus;
import com.tanre.document_register.config.RestTemplateConfig;
import com.tanre.document_register.model.*;
import com.tanre.document_register.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ClaimDocumentService {
    final private ClaimDocumentRepository claimDocumentRepository;
    final private ClaimDocumentFileRepository claimDocumentFileRepository;
    final private ClaimDocumentStatusRepository claimDocumentStatusRepository;
    final private ClaimDocTransactionRepo claimDocTransactionRepo;
    final private ClaimDocFinanceStatusRepo claimDocFinanceStatusRepo;



    private static final Logger logger = LoggerFactory.getLogger(ClaimDocumentService.class);


    @Value("${oracle.service.url}")
    private String oracleServiceUrl;

    private RestTemplate restTemplate;

    public ClaimDocumentService(RestTemplate restTemplate
            ,ClaimDocFinanceStatusRepo claimDocFinanceStatusRepo,ClaimDocumentRepository claimDocumentRepository,ClaimDocumentFileRepository claimDocumentFileRepository, ClaimDocumentStatusRepository claimDocumentStatusRepository, ClaimDocTransactionRepo claimDocTransactionRepo) {
        this.claimDocumentRepository = claimDocumentRepository;
        this.claimDocumentFileRepository = claimDocumentFileRepository;
        this.claimDocumentStatusRepository = claimDocumentStatusRepository;
        this.claimDocTransactionRepo = claimDocTransactionRepo;
        this.claimDocFinanceStatusRepo = claimDocFinanceStatusRepo;
        this.restTemplate = restTemplate;
    }


    @Transactional
    public ClaimDocuments createClaimDocument(
                           ClaimDocumentDTO claimDocumentDTO,
                                   List<MultipartFile> files) throws IOException {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Optional<ClaimDocuments> existingDoc = claimDocumentRepository.findByClaimNumber(claimDocumentDTO.getClaimNumber());

        if (existingDoc.isPresent()) {
            throw new IllegalArgumentException("A document for this claim number already exists.");
        }

        ClaimDocumentStatus defaultStatus = claimDocumentStatusRepository.findByName("PENDING_PAYMENT")
                .orElseThrow(() -> new RuntimeException("Default status 'PENDING_PAYMENT' not found"));


        ClaimDocuments claimDocuments = new ClaimDocuments();
        claimDocuments.setInsured(claimDocumentDTO.getInsuredName());
        claimDocuments.setClaimNumber(claimDocumentDTO.getClaimNumber());
        claimDocuments.setLossDate(claimDocumentDTO.getLossDate());
        claimDocuments.setUnderwritingYear(claimDocumentDTO.getUnderwritingYear());
        claimDocuments.setContractNumber(claimDocumentDTO.getContractNumber());
        claimDocuments.setBrokerCedant(claimDocumentDTO.getBrokerCedant());
        claimDocuments.setSequenceNo(claimDocumentDTO.getSequenceNumber());
        claimDocuments.setStatus(defaultStatus);
        ClaimDocuments savedClaimDocuments = claimDocumentRepository.save(claimDocuments);


        for (MultipartFile file : files) {
            ClaimDocumentFile df = new ClaimDocumentFile();
            df.setClaimDocuments(savedClaimDocuments);
            df.setFileName(file.getOriginalFilename());
            df.setContentType(file.getContentType());
            df.setData(file.getBytes());
            System.out.println("Data field type: " + df.getData().getClass().getName());
//            System.out.println("File bytes: " + Arrays.toString(file.getBytes()));
//            System.out.println("File size: " + file.getSize());
            claimDocumentFileRepository.save(df);
        }

        // *** SEND EMAIL ***
//        emailService.sendDocumentSubmissionEmail(saved);
        return savedClaimDocuments;
    }

    public List<ClaimDocuments> search() {
        return claimDocumentRepository.findAll();
    }

    public List<ClaimDocuments> pendingClaimPayment() {
        return claimDocumentRepository.findByStatus_IdNot(3L);
    }

    public List<ClaimFinanceDocStatus> claimDocumentsListWithFinanceStatus() {
        List<ClaimDocuments> claimDocuments = claimDocumentRepository.findByStatus_Id(3L);
        List<ClaimFinanceDocStatus> resultList = new ArrayList<>();

        for (ClaimDocuments claimDocument : claimDocuments) {
            ClaimDocTransaction claimDocTransaction = getLatestStatus(claimDocument.getId());
            if (claimDocTransaction == null) {
                continue; // or handle as needed
            }


            Optional<ClaimDocumentFinanceStatus> optionalStatus =
                    claimDocFinanceStatusRepo.findById(claimDocTransaction.getToFinanceStatus().getId());

            if (optionalStatus.isEmpty()) {
                continue; // or handle as needed
            }

            ClaimFinanceDocStatus claimFinanceDocStatus = new ClaimFinanceDocStatus();
            claimFinanceDocStatus.setClaimDocuments(claimDocument);
            claimFinanceDocStatus.setClaimDocumentFinanceStatus(optionalStatus.get());

            resultList.add(claimFinanceDocStatus);
        }

        return resultList;
    }

    public List<ClaimDocumentFinanceStatus> getClaimDocFinanceStatus() {
        return claimDocFinanceStatusRepo.findAll();
    }

    public ClaimDocumentStatus getClaimStatus(Long statusId) {
        return claimDocumentStatusRepository.findById(statusId)
               .orElseThrow(() -> new RuntimeException("Status  not found"));
    }

    public ClaimDocTransaction getLatestStatus(Long documentId) {
        return claimDocTransactionRepo.findTopByDocumentIdOrderByChangedAtDesc(documentId)
                .orElseThrow(() -> new RuntimeException("No status history for document"));
    }




    public void processDocument(Long docId, Long statusID) {


        //Get document to be changed
        ClaimDocuments doc = claimDocumentRepository.findById(docId)
                .orElseThrow(() -> new EntityNotFoundException("Doc not found"));

        //assign current status
        ClaimDocumentStatus oldStatus = doc.getStatus();

        //get new status the user want to change to
        ClaimDocumentStatus newStatus = getClaimStatus(statusID);

        //Change the selected document Status
        doc.setStatus(newStatus);

        claimDocumentRepository.save(doc);

        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        ClaimDocTransaction claimDocTransaction = new ClaimDocTransaction();
        claimDocTransaction.setDocument(doc);
        claimDocTransaction.setFromStatus(oldStatus);
        claimDocTransaction.setToStatus(newStatus);
        claimDocTransaction.setChangedBy(user);
        claimDocTransactionRepo.save(claimDocTransaction);
    }


    @Transactional
    public void changeFinanceStatus(Long docId, String comment, Long statusID, Long mainStatusID) {

        //Get document to be changed
        ClaimDocuments doc = claimDocumentRepository.findById(docId)
                .orElseThrow(() -> new EntityNotFoundException("Doc not found"));

        //assign current status
        ClaimDocumentStatus oldMainStatus = doc.getStatus();

        //get new status the user want to change to
        ClaimDocumentStatus newMainStatus = getClaimStatus(mainStatusID);

        // Fetch (finance status)
        ClaimDocumentFinanceStatus newStatus = claimDocFinanceStatusRepo.findById(statusID)
                .orElseThrow(() -> new EntityNotFoundException("Finance sub-status not found"));

        // Get the latest finance status from transaction history (if any)
        ClaimDocTransaction latestTx = claimDocTransactionRepo
                .findTopByDocumentIdOrderByChangedAtDesc(docId)
                .orElse(null);

        ClaimDocumentFinanceStatus oldStatus = (latestTx != null)
                ? latestTx.getToFinanceStatus()
                : null;

        //Change the selected document Status
        doc.setStatus(newMainStatus);
        claimDocumentRepository.save(doc);

        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        ClaimDocTransaction claimDocTransaction = new ClaimDocTransaction();
        claimDocTransaction.setDocument(doc);
        claimDocTransaction.setFromFinanceStatus(oldStatus);
        claimDocTransaction.setToFinanceStatus(newStatus);
        claimDocTransaction.setFromStatus(oldMainStatus);
        claimDocTransaction.setToStatus(newMainStatus);
        claimDocTransaction.setComment(comment);
        claimDocTransaction.setChangedBy(user);
        claimDocTransactionRepo.save(claimDocTransaction);

    }

    public void uploadClaimDocument(
            Long docId,
            List<MultipartFile> files) throws IOException {

        ClaimDocuments doc = claimDocumentRepository.findById(docId)
                .orElseThrow(() -> new EntityNotFoundException("Doc not found"));


        for (MultipartFile file : files) {
            ClaimDocumentFile df = new ClaimDocumentFile();
            df.setClaimDocuments(doc);
            df.setFileName(file.getOriginalFilename());
            df.setContentType(file.getContentType());
            df.setData(file.getBytes());
            claimDocumentFileRepository.save(df);
        }

    }

    public List<ClaimDocFileDTO> getAllClaimDocFiles(Long docId) {
        return claimDocumentFileRepository.findAllByClaimDocuments_Id(docId)
                .stream()
                .map(file -> new ClaimDocFileDTO(
                file.getId(),
                        file.getFileName(),
                        file.getContentType(),
                        file.getDateUploaded()
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public void updateDocumentStatusesBatch(List<ClaimDocumentUpdateRequest> documents) {
        // Prepare batch request
        List<ClaimDocumentUpdateRequest> checkRequests = documents.stream()
                .map(doc -> new ClaimDocumentUpdateRequest(
                        doc.getContractNumber(),
                        doc.getClaimNumber(),
                        doc.getUnderwritingYear(),
                        doc.getDocumentId()))
                .collect(Collectors.toList());
        try {
            // Single API call for all documents
            String url = oracleServiceUrl + "/api/claims/check-batch";

            // Log outgoing request for debugging
            logger.info("Sending batch request to Oracle Service API: {}", url);
            logger.debug("Request payload: {}", checkRequests);


            ResponseEntity<Map> response = restTemplate.postForEntity(
                    url, checkRequests, Map.class);

            Map<String, Boolean> outstandingResults = response.getBody();

            // Validate the API response
            if (outstandingResults == null) {
                logger.error("Oracle Service API returned null response body.");
                throw new RuntimeException("Oracle Service API returned null response");
            }

            // Log additional details for debugging
            logger.debug("API Response from Oracle Service: {}", outstandingResults);


            // Process results and update documents
            List<Long> documentsToComplete = new ArrayList<>();
            for (ClaimDocumentUpdateRequest doc : documents) {
                String key = doc.getContractNumber() + "_" +
                        doc.getClaimNumber() + "_" +
                        doc.getUnderwritingYear();

                // Log key processing for troubleshooting
                logger.debug("Processing document ID: {} with lookup key: {}", doc.getDocumentId(), key);


                Boolean hasOutstanding = outstandingResults.get(key);

                if (hasOutstanding != null && !hasOutstanding) {
                    documentsToComplete.add(doc.getDocumentId());
                }
            }

            // Log documents ready for status update
            logger.info("Documents to be marked as completed: {}", documentsToComplete);

            if (!documentsToComplete.isEmpty()) {
                // Fetch the ClaimDocumentStatus entity for status ID 5L
                ClaimDocumentStatus newStatus = claimDocumentStatusRepository.findById(5L)
                        .orElseThrow(() -> new RuntimeException("Status with ID 5 not found"));


                claimDocumentRepository.updateStatusBatch(documentsToComplete, newStatus);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update document statuses", e);
        }
    }



}
