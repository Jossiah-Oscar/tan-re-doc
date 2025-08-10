package com.tanre.document_register.repository;

import com.tanre.document_register.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, String> {}

