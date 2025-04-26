package com.tanre.document_register.repository;

import com.tanre.document_register.model.BrokerCedant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BrokerCedantRepository extends JpaRepository<BrokerCedant, String> {
}