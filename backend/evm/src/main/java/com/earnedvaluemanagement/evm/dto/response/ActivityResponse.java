package com.earnedvaluemanagement.evm.dto.response;

import java.math.BigDecimal;

public record ActivityResponse(
        Long id,
        String name,
        BigDecimal budgetAtCompletion,
        BigDecimal plannedProgress,
        BigDecimal actualProgress,
        BigDecimal actualCost,
        EvmIndicatorsResponse indicators
) {
}
