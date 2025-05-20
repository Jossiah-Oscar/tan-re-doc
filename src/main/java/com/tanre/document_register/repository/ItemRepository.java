package com.tanre.document_register.repository;


import com.tanre.document_register.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}