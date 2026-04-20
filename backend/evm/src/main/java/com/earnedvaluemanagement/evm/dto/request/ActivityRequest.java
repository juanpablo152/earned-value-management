package com.earnedvaluemanagement.evm.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ActivityRequest(

        @NotBlank(message = "Activity name is required")
        @Size(max = 255, message = "Activity name must not exceed 255 characters")
        String name,

        @NotNull(message = "Budget at completion (BAC) is required")
        @DecimalMin(value = "0.0", message = "Budget at completion must be zero or positive")
        BigDecimal budgetAtCompletion,

        @NotNull(message = "Planned progress is required")
        @DecimalMin(value = "0.0", message = "Planned progress must be between 0 and 100")
        @DecimalMax(value = "100.0", message = "Planned progress must be between 0 and 100")
        BigDecimal plannedProgress,

        @NotNull(message = "Actual progress is required")
        @DecimalMin(value = "0.0", message = "Actual progress must be between 0 and 100")
        @DecimalMax(value = "100.0", message = "Actual progress must be between 0 and 100")
        BigDecimal actualProgress,

        @NotNull(message = "Actual cost (AC) is required")
        @DecimalMin(value = "0.0", message = "Actual cost must be zero or positive")
        BigDecimal actualCost
) {
}
