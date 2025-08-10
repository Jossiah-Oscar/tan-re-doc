package com.tanre.document_register.controller;


import com.tanre.document_register.model.Currency;
import com.tanre.document_register.repository.CurrencyRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    private final CurrencyRepository repo;

    public CurrencyController(CurrencyRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Currency> getAll() {
        return repo.findAll();
    }
}
