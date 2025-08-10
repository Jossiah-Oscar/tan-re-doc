package com.tanre.document_register.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FacultativeOfferCalcRequestDto {
    private Long businessClassId;
    private Long contractTypeId;
    private LocalDate periodFrom;        // so we can derive the year
    private BigDecimal exchangeRate;
    private BigDecimal sumInsuredOs;
    private BigDecimal premiumOs;
    private BigDecimal shareOfferedPct;
    private BigDecimal shareAcceptedPct;
}
