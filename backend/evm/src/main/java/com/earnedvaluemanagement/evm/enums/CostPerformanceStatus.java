package com.earnedvaluemanagement.evm.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CostPerformanceStatus {

    UNDER_BUDGET("Under budget: the project is spending less than planned for work completed"),
    ON_BUDGET("On budget: the project is spending exactly as planned"),
    OVER_BUDGET("Over budget: the project is spending more than planned for work completed"),
    NOT_AVAILABLE("Not available: insufficient data to determine cost performance");

    private final String interpretation;
}
