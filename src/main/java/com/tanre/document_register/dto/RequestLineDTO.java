package com.tanre.document_register.dto;



public record RequestLineDTO(
        Long itemId,
        Integer quantity,
        String reason
) {}
