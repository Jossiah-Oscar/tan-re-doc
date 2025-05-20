package com.tanre.document_register.dto;

public record RequestLineResponseDTO(
        String itemName,
        Integer quantity,
        String reason
) {}