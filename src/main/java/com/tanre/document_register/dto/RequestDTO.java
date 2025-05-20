package com.tanre.document_register.dto;

import java.time.Instant;
import java.util.List;

public record RequestDTO(
        Long id,
        String createdBy,
        Instant createdAt,
        List<RequestLineResponseDTO> lines
) {}