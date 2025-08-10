package com.tanre.document_register.repository;

import com.tanre.document_register.model.FacultativeOfferAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacultativeOfferAnalysisRepository
        extends JpaRepository<FacultativeOfferAnalysis, Long> {
}