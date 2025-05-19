package com.tanre.document_register.dto;

import java.util.List;

public record UserCreateRequest(String username, List<String> roles) {}