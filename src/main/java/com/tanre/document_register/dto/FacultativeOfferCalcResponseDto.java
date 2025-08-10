package com.tanre.document_register.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Setter
@Getter
public class FacultativeOfferCalcResponseDto {
    // echoes of inputs (optional, but often useful)
    private Long businessClassId;
    private Long contractTypeId;

    // 1) Converted to TZS - CHANGED: Match frontend field naming (singular Tz)
    private BigDecimal sumInsuredTz;    // Changed from sumInsuredTzs
    private BigDecimal premiumTz;       // Changed from premiumTzs

    // 2) Shares - CHANGED: Match frontend field naming (singular Tz)
    private BigDecimal soExposureTz;    // Changed from soExposureTzs
    private BigDecimal soPremiumTz;     // Changed from soPremiumTzs
    private BigDecimal saExposureTz;    // Changed from saExposureTzs
    private BigDecimal saPremiumTz;     // Changed from saPremiumTzs

    // 3) TAN-RE - CHANGED: Match frontend field naming (singular Tz)
    private BigDecimal tanReRetentionPct;
    private BigDecimal tanReRetExposureTz;  // Changed from tanReRetExposureTzs
    private BigDecimal tanReRetPremiumTz;   // Changed from tanReRetPremiumTzs

    // 4) Surplus Retro - CHANGED: Match frontend field naming (singular Tz)
    private BigDecimal suRetroPct;
    private BigDecimal suRetroExposureTz;   // Changed from suRetroExposureTzs
    private BigDecimal suRetroPremiumTz;    // Changed from suRetroPremiumTzs

    // 5) Facultative Retro - ADDED: Missing fields from calculation logic
    private BigDecimal facRetroPct;         // ADDED: Missing percentage field
    private BigDecimal facRetroExposureTz;  // Changed from facRetroExposureTzs
    private BigDecimal facRetroPremiumTz;   // ADDED: Missing premium field

    // ADDED: Status and message fields for better error handling
    private String calculationStatus;       // e.g., "SUCCESS", "WARNING", "ERROR"
    private String message;                 // Any calculation notes or warnings

    // ADDED: Program details for transparency (optional but useful)
    private BigDecimal retentionAmount;     // The retention amount used from program
    private BigDecimal treatyLimit;         // The treaty limit used from program
    private BigDecimal totalTreatyCapacity; // Total capacity (retention + treaty limit)
}