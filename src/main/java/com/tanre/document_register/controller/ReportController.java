package com.tanre.document_register.controller;


import com.tanre.document_register.repository.ReportDAO;
import com.tanre.document_register.service.ReportService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService svc;


    public ReportController(ReportService svc) {
        this.svc = svc;
    }


    @GetMapping(value = "/claim-documents",
            produces = "application/vnd.openxmlformats-officedocument" +
                    ".spreadsheetml.sheet")
    public ResponseEntity<InputStreamResource> fetchClaimDocumentReport(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,

            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) throws IOException {
        ByteArrayInputStream in = svc.fetchClaimDocumentReport(start, end);

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String filename = "Claim Document Report" + " " + date + ".xlsx";

        InputStreamResource resource = new InputStreamResource(in);
        ContentDisposition disposition = ContentDisposition.builder("attachment")
                .filename(filename)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        disposition.toString())
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }



    @GetMapping(value = "/debit-note",
            produces = "application/vnd.openxmlformats-officedocument" +
                    ".spreadsheetml.sheet")
    public ResponseEntity<InputStreamResource> fetchDebitNoteReport(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,

            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,

            @RequestParam(value = "userName") String userName
    ) throws IOException {
        ByteArrayInputStream in = svc.fetchDebitNoteReport(start, end, userName);

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String filename = "Debit-Credit-Note-Report" + " " + date + ".xlsx";

        InputStreamResource resource = new InputStreamResource(in);
        ContentDisposition disposition = ContentDisposition.builder("attachment")
                .filename(filename)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        disposition.toString())
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }
}
