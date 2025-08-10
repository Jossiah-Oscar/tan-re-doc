package com.tanre.document_register.controller;


import com.tanre.document_register.model.BusinessClass;
import com.tanre.document_register.repository.BusinessClassRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/business-classes")
public class BusinessClassController {
    private final BusinessClassRepository repo;

    public BusinessClassController(BusinessClassRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<BusinessClass> getAll() {
        return repo.findAll();
    }
}
