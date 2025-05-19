package com.tanre.document_register.dto;

import java.util.List;

public record UserDTO(String username, List<String> roles) {
}