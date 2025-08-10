package com.tanre.document_register.repository;

import com.tanre.document_register.model.AutoFacRetroProgram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutoFacRetroProgramRepository extends JpaRepository<AutoFacRetroProgram, Long> {
    Optional<AutoFacRetroProgram> findByBusinessClassIdAndYear(
            Long businessClassId,
            Integer year
    );
}
