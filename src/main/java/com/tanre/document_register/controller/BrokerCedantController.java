package com.tanre.document_register.controller;

import com.tanre.document_register.dto.BrokerCedantResponse;
import com.tanre.document_register.model.BrokerCedant;
import com.tanre.document_register.repository.BrokerCedantRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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

    @GetMapping("/brokers")
    public List<BrokerCedantResponse> brokers() {
         List<BrokerCedant> brokerCedants = repo.findBrokerCedantByType("B");
         List<BrokerCedantResponse> brokers = new ArrayList<>();
         for(BrokerCedant bc : brokerCedants) {
             BrokerCedantResponse dto = new BrokerCedantResponse();
             dto.setBrokerCedantCode(bc.getCode());
             dto.setBrokerCedantName(bc.getName());
             brokers.add(dto);
         }
        return brokers;
    }

    @GetMapping("/cedants")
    public List<BrokerCedantResponse> cedants() {
        List<BrokerCedant> brokerCedants = repo.findBrokerCedantByType("C");
        List<BrokerCedantResponse> cedants = new ArrayList<>();
        for(BrokerCedant bc : brokerCedants) {
            BrokerCedantResponse dto = new BrokerCedantResponse();
            dto.setBrokerCedantCode(bc.getCode());
            dto.setBrokerCedantName(bc.getName());
            cedants.add(dto);
        }
        return cedants;
    }
}