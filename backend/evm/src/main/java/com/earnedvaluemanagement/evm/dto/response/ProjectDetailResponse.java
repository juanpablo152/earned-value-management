package com.earnedvaluemanagement.evm.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectDetailResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ActivityResponse> activities,
        EvmIndicatorsResponse consolidatedIndicators
) {
}
