package com.tanre.document_register.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;


@Entity
@Table(name = "facultative_offer_analysis")
@Getter
@Setter
public class FacultativeOfferAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. Cedant
    @Column(nullable = false, length = 100)
    private String cedant;

    // 2. Date Time Created
    @Column(name = "date_time_created", nullable = false)
    private LocalDateTime dateTimeCreated;

    // 3. Insured
    @Column(nullable = false, length = 100)
    private String insured;

    // 4. Occupation
    @Column(nullable = false, length = 100)
    private String occupation;

    // 5. Class (FK to business_class.id)
    @Column(name = "business_class_id", nullable = false)
    private Long businessClassId;

    // 5. Class (FK to business_class.id)
    @Column(name = "contract_type_id", nullable = false)
    private Long contractTypeId;

    // 6. Country
    @Column(nullable = false, length = 50)
    private String country;

    // 7. Period – from/to
    @Column(name = "period_from", nullable = false)
    private LocalDate periodFrom;
    @Column(name = "period_to", nullable = false)
    private LocalDate periodTo;

    // 8. Currency (FK to currency.code)
    @Column(name = "currency_code", nullable = false, length = 10)
    private String currencyCode;

    // 9. Sum Insured – Provided by the cedant
    @Column(name = "sum_insured_os", precision = 30, scale = 2)
    private BigDecimal sumInsuredOs;

    // 10. Premium (O/S) – Provided by the cedant
    @Column(name = "premium_os", precision = 30, scale = 2)
    private BigDecimal premiumOs;

    // **New**: 11a. Exchange Rate used for conversion
    @Column(name = "exchange_rate", precision = 18, scale = 8, nullable = false)
    private BigDecimal exchangeRate;

    // 11. Sum Insured In TZS = sumInsuredOs * exchangeRate
    @Column(name = "sum_insured_tzs", precision = 30, scale = 2)
    private BigDecimal sumInsuredTzs;

    // 12. Premium in TZ = premiumOs * exchangeRate
    @Column(name = "premium_tz", precision = 30, scale = 2)
    private BigDecimal premiumTzs;

    // 13. Share Offered (in %) – Input from the underwriter
    @Column(name = "share_offered_pct", precision = 5, scale = 2)
    private BigDecimal shareOfferedPct;

    // 14. SO – Exposure TZS = shareOfferedPct/100 * sumInsuredTz
    @Column(name = "so_exposure_tzs", precision = 30, scale = 2)
    private BigDecimal soExposureTzs;

    // 15. SO – Premium TZS = shareOfferedPct/100 * premiumTz
    @Column(name = "so_premium_tzs", precision = 30, scale = 2)
    private BigDecimal soPremiumTzs;

    // 16. Shared Accepted (in % ≤ shareOfferedPct)
    @Column(name = "share_accepted_pct", precision = 5, scale = 2)
    private BigDecimal shareAcceptedPct;

    // 17. SA – Exposure TZS = shareAcceptedPct/100 * sumInsuredTz
    @Column(name = "sa_exposure_tzs", precision = 30, scale = 2)
    private BigDecimal saExposureTzs;

    // 18. SA – Premium TZS = shareAcceptedPct/100 * premiumTz
    @Column(name = "sa_premium_tzs", precision = 30, scale = 2)
    private BigDecimal saPremiumTzs;

    // 19. TAN-RE Retention (%) – computed in service
    @Column(name = "tan_re_retention_pct", precision = 5, scale = 2)
    private BigDecimal tanReRetentionPct;

    // 20. TAN-RE RET Exposure (TZS)
    @Column(name = "tan_re_ret_exposure_tzs", precision = 30, scale = 2)
    private BigDecimal tanReRetExposureTzs;

    // 21. TAN-RE RET Premium = saPremiumTz * tanReRetentionPct/100
    @Column(name = "tan_re_ret_premium_tzs", precision = 30, scale = 2)
    private BigDecimal tanReRetPremiumTzs;

    // 22. First SU Retro (%) – computed in service
    @Column(name = "su_retro_pct", precision = 5, scale = 2)
    private BigDecimal suRetroPct;

    // 23. First SU Exposure TZS
    @Column(name = "su_retro_exposure_tzs", precision = 30, scale = 2)
    private BigDecimal suRetroExposureTzs;

    // 24. First SU Premium TZS (optional)
    @Column(name = "su_retro_premium_tzs", precision = 30, scale = 2)
    private BigDecimal suRetroPremiumTzs;

    // 25. Fac Retro Exposure TZS
    @Column(name = "fac_retro_pct", precision = 5, scale = 2)
    private BigDecimal facRetroPct;
    @Column(name = "fac_retro_exposure_tzs", precision = 30, scale = 2)
    private BigDecimal facRetroExposureTzs;
    @Column(name = "fac_retro_premium_tzs", precision = 30, scale = 2)
    private BigDecimal facRetroPremiumTzs;
}
