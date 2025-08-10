package com.tanre.document_register.service;

import com.tanre.document_register.dto.FacultativeOfferCalcRequestDto;
import com.tanre.document_register.dto.FacultativeOfferCalcResponseDto;
import com.tanre.document_register.model.AutoFacRetroProgram;
import com.tanre.document_register.model.FacultativeOfferAnalysis;
import com.tanre.document_register.model.SurplusRetroProgram;
import com.tanre.document_register.repository.AutoFacRetroProgramRepository;
import com.tanre.document_register.repository.FacultativeOfferAnalysisRepository;
import com.tanre.document_register.repository.SurplusRetroProgramRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class FacultativeOfferAnalysisService {

    private final FacultativeOfferAnalysisRepository repo;
    private final AutoFacRetroProgramRepository autoFacRepo;
    private final SurplusRetroProgramRepository surplusRepo;

    public FacultativeOfferAnalysisService(FacultativeOfferAnalysisRepository repo, AutoFacRetroProgramRepository autoFacRepo, SurplusRetroProgramRepository surplusRepo) {
        this.repo = repo;
        this.autoFacRepo = autoFacRepo;
        this.surplusRepo = surplusRepo;
    }



//    ----------METHODS START HERE-------------

    public List<FacultativeOfferAnalysis> findAll() {
        return repo.findAll();
    }

    public Optional<FacultativeOfferAnalysis> findById(Long id) {
        return repo.findById(id);
    }

    public FacultativeOfferAnalysis save(FacultativeOfferAnalysis analysis) {
        return repo.save(analysis);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    public FacultativeOfferCalcResponseDto calculate(FacultativeOfferCalcRequestDto request) {
        BigDecimal rate = request.getExchangeRate();
        BigDecimal pctDivisor = BigDecimal.valueOf(100);

        // Create response DTO to populate
        FacultativeOfferCalcResponseDto response = new FacultativeOfferCalcResponseDto();

        // Echo the input identifiers
        response.setBusinessClassId(request.getBusinessClassId());
        response.setContractTypeId(request.getContractTypeId());

        // Validate required inputs
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            response.setCalculationStatus("ERROR");
            response.setMessage("Exchange rate is required and must be greater than 0");
            return response;
        }

        // 1) Convert cedant values to TZS
        if (request.getSumInsuredOs() != null) {
            response.setSumInsuredTz(request.getSumInsuredOs().multiply(rate));
        }
        if (request.getPremiumOs() != null) {
            response.setPremiumTz(request.getPremiumOs().multiply(rate));
        }

        // 2) Shares offered and accepted exposure and premium
        BigDecimal offeredCoef = Optional.ofNullable(request.getShareOfferedPct())
                .map(p -> p.divide(pctDivisor, 8, RoundingMode.HALF_UP))
                .orElse(BigDecimal.ZERO);
        BigDecimal acceptedCoef = Optional.ofNullable(request.getShareAcceptedPct())
                .map(p -> p.divide(pctDivisor, 8, RoundingMode.HALF_UP))
                .orElse(BigDecimal.ZERO);

        if (response.getSumInsuredTz() != null) {
            response.setSoExposureTz(response.getSumInsuredTz().multiply(offeredCoef));
            response.setSaExposureTz(response.getSumInsuredTz().multiply(acceptedCoef));
        }
        if (response.getPremiumTz() != null) {
            response.setSoPremiumTz(response.getPremiumTz().multiply(offeredCoef));
            response.setSaPremiumTz(response.getPremiumTz().multiply(acceptedCoef));
        }

        // 3) TAN-RE retention: fetch program data for the specified contract type
        SurplusRetroProgram contractProgram = null;
        if (request.getBusinessClassId() != null && request.getContractTypeId() != null && request.getPeriodFrom() != null) {
            contractProgram = surplusRepo
                    .findByBusinessClassIdAndContractTypeIdAndYear(
                            request.getBusinessClassId(),
                            request.getContractTypeId(),
                            request.getPeriodFrom().getYear())
                    .orElse(null);
        }

        if (contractProgram != null && response.getSaExposureTz() != null && response.getSaPremiumTz() != null) {
            // Get retention amount per contract type
            BigDecimal retention = BigDecimal.valueOf(contractProgram.getRetention());

            // Calculate TAN-RE retention exposure (cannot exceed SA exposure or retention limit)
            BigDecimal tanReExposure = response.getSaExposureTz().min(retention);
            response.setTanReRetExposureTz(tanReExposure);

            // Calculate TAN-RE retention percentage based on SA exposure
            if (response.getSaExposureTz().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal tanReRetentionPct = tanReExposure
                        .divide(response.getSaExposureTz(), 8, RoundingMode.HALF_UP)
                        .multiply(pctDivisor); // Convert back to percentage
                response.setTanReRetentionPct(tanReRetentionPct);

                // Calculate TAN-RE retention premium
                response.setTanReRetPremiumTz(response.getSaPremiumTz().multiply(
                        tanReRetentionPct.divide(pctDivisor, 8, RoundingMode.HALF_UP)));
            }

            // 4) SU Retro (Surplus Retro) - handles excess over TAN-RE retention
            BigDecimal excessFromTanRe = response.getSaExposureTz().subtract(retention);

            if (excessFromTanRe.compareTo(BigDecimal.ZERO) > 0) {
                // There's excess exposure beyond TAN-RE retention
                BigDecimal treatyLimit = BigDecimal.valueOf(contractProgram.getTreatyLimit()); // Adjust field name as needed

                // SU Retro takes the lesser of excess or treaty limit
                BigDecimal suRetroExposure = excessFromTanRe.min(treatyLimit);
                response.setSuRetroExposureTz(suRetroExposure);

                // Calculate SU Retro percentage based on SA exposure
                if (response.getSaExposureTz().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal suRetroPct = suRetroExposure
                            .divide(response.getSaExposureTz(), 8, RoundingMode.HALF_UP)
                            .multiply(pctDivisor); // Convert back to percentage
                    response.setSuRetroPct(suRetroPct);

                    // Calculate SU Retro premium
                    response.setSuRetroPremiumTz(response.getSaPremiumTz().multiply(
                            suRetroPct.divide(pctDivisor, 8, RoundingMode.HALF_UP)));
                }

                // 5) Fac Retro - handles excess over both TAN-RE and SU Retro
                BigDecimal totalTreatyCapacity = retention.add(treatyLimit);
                BigDecimal excessFromTreaty = response.getSaExposureTz().subtract(totalTreatyCapacity);

                if (excessFromTreaty.compareTo(BigDecimal.ZERO) > 0) {
                    // There's excess exposure beyond treaty capacity - goes to Fac Retro
                    response.setFacRetroExposureTz(excessFromTreaty);

                    // Calculate Fac Retro percentage based on SA exposure
                    BigDecimal facRetroPct = excessFromTreaty
                            .divide(response.getSaExposureTz(), 8, RoundingMode.HALF_UP)
                            .multiply(pctDivisor); // Convert back to percentage
                    response.setFacRetroPct(facRetroPct);

                    // Calculate Fac Retro premium
                    response.setFacRetroPremiumTz(response.getSaPremiumTz().multiply(
                            facRetroPct.divide(pctDivisor, 8, RoundingMode.HALF_UP)));
                } else {
                    // No excess - no Fac Retro needed
                    response.setFacRetroExposureTz(BigDecimal.ZERO);
                    response.setFacRetroPct(BigDecimal.ZERO);
                    response.setFacRetroPremiumTz(BigDecimal.ZERO);
                }

                // Set program details for transparency
                response.setRetentionAmount(retention);
                response.setTreatyLimit(treatyLimit);
                response.setTotalTreatyCapacity(totalTreatyCapacity);
            } else {
                // No excess over TAN-RE retention - no SU Retro or Fac Retro needed
                response.setSuRetroExposureTz(BigDecimal.ZERO);
                response.setSuRetroPct(BigDecimal.ZERO);
                response.setSuRetroPremiumTz(BigDecimal.ZERO);
                response.setFacRetroExposureTz(BigDecimal.ZERO);
                response.setFacRetroPct(BigDecimal.ZERO);
                response.setFacRetroPremiumTz(BigDecimal.ZERO);

                // Set program details
                response.setRetentionAmount(retention);
                response.setTreatyLimit(BigDecimal.valueOf(contractProgram.getTreatyLimit()));
                response.setTotalTreatyCapacity(response.getRetentionAmount().add(response.getTreatyLimit()));
            }
        } else {
            // No contract program found or missing SA values - set all retro values to zero
            response.setTanReRetExposureTz(BigDecimal.ZERO);
            response.setTanReRetentionPct(BigDecimal.ZERO);
            response.setTanReRetPremiumTz(BigDecimal.ZERO);
            response.setSuRetroExposureTz(BigDecimal.ZERO);
            response.setSuRetroPct(BigDecimal.ZERO);
            response.setSuRetroPremiumTz(BigDecimal.ZERO);
            response.setFacRetroExposureTz(BigDecimal.ZERO);
            response.setFacRetroPct(BigDecimal.ZERO);
            response.setFacRetroPremiumTz(BigDecimal.ZERO);

            if (contractProgram == null) {
                response.setCalculationStatus("WARNING");
                response.setMessage("No surplus retrocession program found for the specified business class, contract type, and year");
            }
        }

        // Set final status if not already set
        if (response.getCalculationStatus() == null) {
            response.setCalculationStatus("SUCCESS");
            response.setMessage("Calculation completed successfully");
        }

        return response;
    }


}
