package com.tanre.document_register.service;

import com.tanre.document_register.dto.RequestLineDTO;
import com.tanre.document_register.model.Item;
import com.tanre.document_register.model.ItemRequest;
import com.tanre.document_register.model.ItemRequestLine;
import com.tanre.document_register.repository.ItemRepository;
import com.tanre.document_register.repository.ItemRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


@Service
public class ItemService {
    private final ItemRepository repo;
    private final ItemRequestRepository requestRepo;

    public ItemService(ItemRepository repo, ItemRequestRepository requestRepo) {
        this.repo = repo;
        this.requestRepo = requestRepo;
    }

    public List<Item> list() {
        return repo.findAll();
    }

    public Item get(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public Item create(Item item) {
        return repo.save(item);
    }

    public Item update(Long id, Item updated) {
        Item existing = get(id);
        existing.setName(updated.getName());
        existing.setQuantity(updated.getQuantity());
        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public ItemRequest createRequest(List<RequestLineDTO> dtos) {
        // 1. Build master request
        ItemRequest req = new ItemRequest();
        req.setCreatedAt(Instant.now());
        // pull username from SecurityContext
        String user = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        req.setCreatedBy(user);

        // 2. For each line DTO, create & attach a request line
        for (var dto : dtos) {
            Item item = repo.findById(dto.itemId())
                    .orElseThrow(() -> new EntityNotFoundException("Item not found"));

            // You can optionally reduce the stock here or leave that's a separate concern

            ItemRequestLine line = new ItemRequestLine();
            line.setRequest(req);
            line.setItem(item);
            line.setQuantity(dto.quantity());
            line.setReason(dto.reason());
            req.getLines().add(line);
        }

        // 3. Persist
        return requestRepo.save(req);
    }

    @Transactional(readOnly = true)
    public List<ItemRequest> getAllRequests() {
        return requestRepo.findAll();
    }
}
