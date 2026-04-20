package com.earnedvaluemanagement.evm.dto.response;

import java.math.BigDecimal;

public record EvmIndicatorsResponse(
        BigDecimal plannedValue,
        BigDecimal earnedValue,
        BigDecimal costVariance,
        BigDecimal scheduleVariance,
        BigDecimal costPerformanceIndex,
        BigDecimal schedulePerformanceIndex,
        BigDecimal estimateAtCompletion,
        BigDecimal varianceAtCompletion,
        String costPerformanceInterpretation,
        String schedulePerformanceInterpretation
) {
}
