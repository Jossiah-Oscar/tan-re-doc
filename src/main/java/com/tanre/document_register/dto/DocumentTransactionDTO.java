package com.tanre.document_register.dto;

import java.time.Instant;

public record DocumentTransactionDTO(
        Long id,
        String oldStatus,
        String newStatus,
        String comment,
        String changedBy,
        Instant changedAt
) {}
