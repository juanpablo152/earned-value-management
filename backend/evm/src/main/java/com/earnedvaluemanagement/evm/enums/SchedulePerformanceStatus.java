package com.earnedvaluemanagement.evm.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SchedulePerformanceStatus {

    AHEAD_OF_SCHEDULE("Ahead of schedule: more work has been completed than planned"),
    ON_SCHEDULE("On schedule: work is progressing exactly as planned"),
    BEHIND_SCHEDULE("Behind schedule: less work has been completed than planned"),
    NOT_AVAILABLE("Not available: insufficient data to determine schedule performance");

    private final String interpretation;
}
