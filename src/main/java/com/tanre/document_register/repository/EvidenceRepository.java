package com.tanre.document_register.repository;

import com.tanre.document_register.model.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {}