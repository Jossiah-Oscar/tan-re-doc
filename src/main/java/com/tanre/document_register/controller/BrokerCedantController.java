package com.tanre.document_register.controller;

import com.tanre.document_register.model.BrokerCedant;
import com.tanre.document_register.repository.BrokerCedantRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/broker-cedants")
@CrossOrigin(origins = "*")

public class BrokerCedantController {
    private final BrokerCedantRepository repo;

    public BrokerCedantController(BrokerCedantRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<BrokerCedant> listAll() {
        return repo.findAll();
    }
}