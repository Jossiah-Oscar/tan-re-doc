package com.tanre.document_register.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeFinanceStatusRequest {
    private Long financeStatusId;
    private Long mainStatusId;
    private String comment;
}
