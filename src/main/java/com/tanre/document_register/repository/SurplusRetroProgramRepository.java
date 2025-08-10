package com.tanre.document_register.repository;

import com.tanre.document_register.model.SurplusRetroProgram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurplusRetroProgramRepository extends JpaRepository<SurplusRetroProgram, Long> {
    Optional<SurplusRetroProgram> findByBusinessClassIdAndContractTypeIdAndYear(
            Long businessClassId,
            Long contractTypeId,
            Integer year
    );
}
