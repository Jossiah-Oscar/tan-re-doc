package com.tanre.document_register.repository;

import com.tanre.document_register.model.BrokerCedant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BrokerCedantRepository extends JpaRepository<BrokerCedant, String> {


    @Query("SELECT b.name FROM BrokerCedant b WHERE b.code = :code")
    String findNameByCode(@Param("code") String code);

    List<BrokerCedant> findBrokerCedantByType(String c);
}