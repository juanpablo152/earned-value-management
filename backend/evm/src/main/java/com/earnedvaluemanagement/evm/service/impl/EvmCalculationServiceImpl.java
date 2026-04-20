package com.earnedvaluemanagement.evm.service.impl;

import com.earnedvaluemanagement.evm.dto.response.EvmIndicatorsResponse;
import com.earnedvaluemanagement.evm.entity.Activity;
import com.earnedvaluemanagement.evm.enums.CostPerformanceStatus;
import com.earnedvaluemanagement.evm.enums.SchedulePerformanceStatus;
import com.earnedvaluemanagement.evm.service.EvmCalculationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class EvmCalculationServiceImpl implements EvmCalculationService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final int MONETARY_SCALE = 2;
    private static final int INDEX_SCALE = 4;

    @Override
    public EvmIndicatorsResponse calculateForActivity(Activity activity) {
        BigDecimal bac = activity.getBudgetAtCompletion();
        BigDecimal pv = percentageOf(activity.getPlannedProgress(), bac);
        BigDecimal ev = percentageOf(activity.getActualProgress(), bac);
        BigDecimal ac = activity.getActualCost();

        return buildIndicators(bac, pv, ev, ac);
    }

    @Override
    public EvmIndicatorsResponse calculateConsolidated(List<Activity> activities) {
        if (activities.isEmpty()) {
            return emptyIndicators();
        }

        BigDecimal totalBac = BigDecimal.ZERO;
        BigDecimal totalPv = BigDecimal.ZERO;
        BigDecimal totalEv = BigDecimal.ZERO;
        BigDecimal totalAc = BigDecimal.ZERO;

        for (Activity activity : activities) {
            BigDecimal bac = activity.getBudgetAtCompletion();
            totalBac = totalBac.add(bac);
            totalPv = totalPv.add(percentageOf(activity.getPlannedProgress(), bac));
            totalEv = totalEv.add(percentageOf(activity.getActualProgress(), bac));
            totalAc = totalAc.add(activity.getActualCost());
        }

        return buildIndicators(totalBac, totalPv, totalEv, totalAc);
    }

    private BigDecimal percentageOf(BigDecimal percentage, BigDecimal total) {
        return percentage
                .divide(ONE_HUNDRED, INDEX_SCALE, RoundingMode.HALF_UP)
                .multiply(total)
                .setScale(MONETARY_SCALE, RoundingMode.HALF_UP);
    }

    private EvmIndicatorsResponse buildIndicators(BigDecimal bac, BigDecimal pv, BigDecimal ev, BigDecimal ac) {
        BigDecimal cv = ev.subtract(ac).setScale(MONETARY_SCALE, RoundingMode.HALF_UP);
        BigDecimal sv = ev.subtract(pv).setScale(MONETARY_SCALE, RoundingMode.HALF_UP);

        BigDecimal cpi = safeDivide(ev, ac, INDEX_SCALE);
        BigDecimal spi = safeDivide(ev, pv, INDEX_SCALE);

        BigDecimal eac = (cpi != null && cpi.compareTo(BigDecimal.ZERO) != 0)
                ? bac.divide(cpi, MONETARY_SCALE, RoundingMode.HALF_UP)
                : null;

        BigDecimal vac = (eac != null)
                ? bac.subtract(eac).setScale(MONETARY_SCALE, RoundingMode.HALF_UP)
                : null;

        return new EvmIndicatorsResponse(
                pv, ev, cv, sv, cpi, spi, eac, vac,
                interpretCpi(cpi).getInterpretation(),
                interpretSpi(spi).getInterpretation()
        );
    }

    private BigDecimal safeDivide(BigDecimal numerator, BigDecimal denominator, int scale) {
        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return numerator.divide(denominator, scale, RoundingMode.HALF_UP);
    }

    private CostPerformanceStatus interpretCpi(BigDecimal cpi) {
        if (cpi == null) {
            return CostPerformanceStatus.NOT_AVAILABLE;
        }
        int comparison = cpi.compareTo(BigDecimal.ONE);
        if (comparison > 0) {
            return CostPerformanceStatus.UNDER_BUDGET;
        }
        if (comparison < 0) {
            return CostPerformanceStatus.OVER_BUDGET;
        }
        return CostPerformanceStatus.ON_BUDGET;
    }

    private SchedulePerformanceStatus interpretSpi(BigDecimal spi) {
        if (spi == null) {
            return SchedulePerformanceStatus.NOT_AVAILABLE;
        }
        int comparison = spi.compareTo(BigDecimal.ONE);
        if (comparison > 0) {
            return SchedulePerformanceStatus.AHEAD_OF_SCHEDULE;
        }
        if (comparison < 0) {
            return SchedulePerformanceStatus.BEHIND_SCHEDULE;
        }
        return SchedulePerformanceStatus.ON_SCHEDULE;
    }

    private EvmIndicatorsResponse emptyIndicators() {
        return new EvmIndicatorsResponse(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                null, null, null, null,
                CostPerformanceStatus.NOT_AVAILABLE.getInterpretation(),
                SchedulePerformanceStatus.NOT_AVAILABLE.getInterpretation()
        );
    }
}
