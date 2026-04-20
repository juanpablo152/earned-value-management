package com.earnedvaluemanagement.evm.service;

import com.earnedvaluemanagement.evm.dto.response.EvmIndicatorsResponse;
import com.earnedvaluemanagement.evm.entity.Activity;

import java.util.List;

public interface EvmCalculationService {

    EvmIndicatorsResponse calculateForActivity(Activity activity);

    EvmIndicatorsResponse calculateConsolidated(List<Activity> activities);
}
