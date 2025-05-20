package com.tanre.document_register.repository;

import com.tanre.document_register.model.ItemTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemTransactionRepository extends JpaRepository<ItemTransaction, Long> {

}