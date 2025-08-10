package com.tanre.document_register.controller;

import com.tanre.document_register.dto.FacultativeOfferCalcRequestDto;
import com.tanre.document_register.dto.FacultativeOfferCalcResponseDto;
import com.tanre.document_register.model.FacultativeOfferAnalysis;
import com.tanre.document_register.service.FacultativeOfferAnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facultative-offer-analysis")
public class FacultativeOfferAnalysisController {

    private final FacultativeOfferAnalysisService facultativeOfferAnalysisService;

    public FacultativeOfferAnalysisController(FacultativeOfferAnalysisService facultativeOfferAnalysisService) {
        this.facultativeOfferAnalysisService = facultativeOfferAnalysisService;
    }

    @GetMapping
    public List<FacultativeOfferAnalysis> getAll() {
        return facultativeOfferAnalysisService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacultativeOfferAnalysis> getById(@PathVariable Long id) {
        return facultativeOfferAnalysisService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FacultativeOfferAnalysis> create(@RequestBody FacultativeOfferAnalysis analysis) {
        FacultativeOfferAnalysis saved = facultativeOfferAnalysisService.save(analysis);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacultativeOfferAnalysis> update(
            @PathVariable Long id,
            @RequestBody FacultativeOfferAnalysis analysis
    ) {
        return facultativeOfferAnalysisService.findById(id)
                .map(existing -> {
                    analysis.setId(id);
                    FacultativeOfferAnalysis updated = facultativeOfferAnalysisService.save(analysis);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return facultativeOfferAnalysisService.findById(id)
                .map(existing -> {
                    facultativeOfferAnalysisService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/calculate")
    public ResponseEntity<FacultativeOfferCalcResponseDto> calculateOffer(
            @RequestBody @Validated FacultativeOfferCalcRequestDto request) {

        try {
            FacultativeOfferCalcResponseDto response = facultativeOfferAnalysisService.calculate(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle any unexpected errors
            FacultativeOfferCalcResponseDto errorResponse = new FacultativeOfferCalcResponseDto();
            errorResponse.setCalculationStatus("ERROR");
            errorResponse.setMessage("Calculation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
