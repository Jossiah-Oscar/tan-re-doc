package com.tanre.document_register.controller;

import com.tanre.document_register.dto.RequestDTO;
import com.tanre.document_register.dto.RequestLineDTO;
import com.tanre.document_register.dto.RequestLineResponseDTO;
import com.tanre.document_register.model.Item;
import com.tanre.document_register.model.ItemRequest;
import com.tanre.document_register.model.ItemRequestLine;
import com.tanre.document_register.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@CrossOrigin // allow your Next.js app to call it
public class ItemController {
    private final ItemService svc;

    public ItemController(ItemService svc) { this.svc = svc; }

    @GetMapping
    public List<Item> list() {
        return svc.list();
    }

    @GetMapping("/{id}") public Item get(@PathVariable Long id) {
        return svc.get(id);
    }

    @PostMapping public Item create(@RequestBody Item item) {
        return svc.create(item);
    }

    @PutMapping("/{id}") public Item update(@PathVariable Long id, @RequestBody Item item) {
        return svc.update(id, item);
    }
    @DeleteMapping("/{id}") public ResponseEntity<?> delete(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/requests")
    public ResponseEntity<Void> requestItems(
            @RequestBody List<RequestLineDTO> lines) {
        svc.createRequest(lines);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/requests")
    public List<RequestDTO> listRequests() {
        return svc.getAllRequests().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private RequestDTO toDto(ItemRequest req) {
        var lines = req.getLines().stream()
                .map(line -> new RequestLineResponseDTO(
                        line.getItem().getName(),   // String
                        line.getQuantity(),
                        line.getReason()
                ))
                .toList();

        return new RequestDTO(
                req.getId(),
                req.getCreatedBy(),
                req.getCreatedAt(),
                lines
        );
    }

}