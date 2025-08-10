package com.tanre.document_register.controller;

import com.tanre.document_register.model.ContractType;
import com.tanre.document_register.repository.ContractTypeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contract-types")
public class ContractTypeController {
    private final ContractTypeRepository repo;

    public ContractTypeController(ContractTypeRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<ContractType> getAll() {
        return repo.findAll();
    }
}