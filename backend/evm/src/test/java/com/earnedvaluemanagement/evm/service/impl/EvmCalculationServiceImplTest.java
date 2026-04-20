package com.earnedvaluemanagement.evm.service.impl;

import com.earnedvaluemanagement.evm.dto.response.EvmIndicatorsResponse;
import com.earnedvaluemanagement.evm.entity.Activity;
import com.earnedvaluemanagement.evm.enums.CostPerformanceStatus;
import com.earnedvaluemanagement.evm.enums.SchedulePerformanceStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EvmCalculationServiceImplTest {

    private final EvmCalculationServiceImpl calculator = new EvmCalculationServiceImpl();

    @Nested
    @DisplayName("Individual activity calculations")
    class ActivityCalculations {

        @Test
        @DisplayName("Over budget and behind schedule: CPI < 1, SPI < 1")
        void shouldCalculateOverBudgetBehindSchedule() {
            Activity activity = buildActivity("10000", "50", "40", "5000");

            EvmIndicatorsResponse result = calculator.calculateForActivity(activity);

            assertThat(result.plannedValue()).isEqualByComparingTo("5000.00");
            assertThat(result.earnedValue()).isEqualByComparingTo("4000.00");
            assertThat(result.costVariance()).isEqualByComparingTo("-1000.00");
            assertThat(result.scheduleVariance()).isEqualByComparingTo("-1000.00");
            assertThat(result.costPerformanceIndex()).isEqualByComparingTo("0.8000");
            assertThat(result.schedulePerformanceIndex()).isEqualByComparingTo("0.8000");
            assertThat(result.estimateAtCompletion()).isEqualByComparingTo("12500.00");
            assertThat(result.varianceAtCompletion()).isEqualByComparingTo("-2500.00");
            assertThat(result.costPerformanceInterpretation())
                    .isEqualTo(CostPerformanceStatus.OVER_BUDGET.getInterpretation());
            assertThat(result.schedulePerformanceInterpretation())
                    .isEqualTo(SchedulePerformanceStatus.BEHIND_SCHEDULE.getInterpretation());
        }

        @Test
        @DisplayName("Under budget and ahead of schedule: CPI > 1, SPI > 1")
        void shouldCalculateUnderBudgetAheadOfSchedule() {
            Activity activity = buildActivity("10000", "40", "60", "3000");

            EvmIndicatorsResponse result = calculator.calculateForActivity(activity);

            assertThat(result.plannedValue()).isEqualByComparingTo("4000.00");
            assertThat(result.earnedValue()).isEqualByComparingTo("6000.00");
            assertThat(result.costVariance()).isEqualByComparingTo("3000.00");
            assertThat(result.scheduleVariance()).isEqualByComparingTo("2000.00");
            assertThat(result.costPerformanceIndex()).isEqualByComparingTo("2.0000");
            assertThat(result.schedulePerformanceIndex()).isEqualByComparingTo("1.5000");
            assertThat(result.estimateAtCompletion()).isEqualByComparingTo("5000.00");
            assertThat(result.varianceAtCompletion()).isEqualByComparingTo("5000.00");
            assertThat(result.costPerformanceInterpretation())
                    .isEqualTo(CostPerformanceStatus.UNDER_BUDGET.getInterpretation());
            assertThat(result.schedulePerformanceInterpretation())
                    .isEqualTo(SchedulePerformanceStatus.AHEAD_OF_SCHEDULE.getInterpretation());
        }

        @Test
        @DisplayName("On budget and on schedule: CPI = 1, SPI = 1")
        void shouldCalculateOnBudgetOnSchedule() {
            Activity activity = buildActivity("10000", "50", "50", "5000");

            EvmIndicatorsResponse result = calculator.calculateForActivity(activity);

            assertThat(result.plannedValue()).isEqualByComparingTo("5000.00");
            assertThat(result.earnedValue()).isEqualByComparingTo("5000.00");
            assertThat(result.costVariance()).isEqualByComparingTo("0.00");
            assertThat(result.scheduleVariance()).isEqualByComparingTo("0.00");
            assertThat(result.costPerformanceIndex()).isEqualByComparingTo("1.0000");
            assertThat(result.schedulePerformanceIndex()).isEqualByComparingTo("1.0000");
            assertThat(result.estimateAtCompletion()).isEqualByComparingTo("10000.00");
            assertThat(result.varianceAtCompletion()).isEqualByComparingTo("0.00");
            assertThat(result.costPerformanceInterpretation())
                    .isEqualTo(CostPerformanceStatus.ON_BUDGET.getInterpretation());
            assertThat(result.schedulePerformanceInterpretation())
                    .isEqualTo(SchedulePerformanceStatus.ON_SCHEDULE.getInterpretation());
        }

        @Test
        @DisplayName("100% completed on budget")
        void shouldCalculateFullyCompletedOnBudget() {
            Activity activity = buildActivity("8000", "100", "100", "8000");

            EvmIndicatorsResponse result = calculator.calculateForActivity(activity);

            assertThat(result.plannedValue()).isEqualByComparingTo("8000.00");
            assertThat(result.earnedValue()).isEqualByComparingTo("8000.00");
            assertThat(result.costVariance()).isEqualByComparingTo("0.00");
            assertThat(result.scheduleVariance()).isEqualByComparingTo("0.00");
            assertThat(result.costPerformanceIndex()).isEqualByComparingTo("1.0000");
            assertThat(result.schedulePerformanceIndex()).isEqualByComparingTo("1.0000");
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("AC = 0: CPI is null, EAC/VAC are null")
        void shouldReturnNullCpiWhenActualCostIsZero() {
            Activity activity = buildActivity("10000", "50", "30", "0");

            EvmIndicatorsResponse result = calculator.calculateForActivity(activity);

            assertThat(result.plannedValue()).isEqualByComparingTo("5000.00");
            assertThat(result.earnedValue()).isEqualByComparingTo("3000.00");
            assertThat(result.costVariance()).isEqualByComparingTo("3000.00");
            assertThat(result.scheduleVariance()).isEqualByComparingTo("-2000.00");
            assertThat(result.costPerformanceIndex()).isNull();
            assertThat(result.schedulePerformanceIndex()).isEqualByComparingTo("0.6000");
            assertThat(result.estimateAtCompletion()).isNull();
            assertThat(result.varianceAtCompletion()).isNull();
            assertThat(result.costPerformanceInterpretation())
                    .isEqualTo(CostPerformanceStatus.NOT_AVAILABLE.getInterpretation());
        }

        @Test
        @DisplayName("Planned progress = 0: SPI is null")
        void shouldReturnNullSpiWhenPlannedProgressIsZero() {
            Activity activity = buildActivity("10000", "0", "30", "2000");

            EvmIndicatorsResponse result = calculator.calculateForActivity(activity);

            assertThat(result.plannedValue()).isEqualByComparingTo("0.00");
            assertThat(result.earnedValue()).isEqualByComparingTo("3000.00");
            assertThat(result.costVariance()).isEqualByComparingTo("1000.00");
            assertThat(result.scheduleVariance()).isEqualByComparingTo("3000.00");
            assertThat(result.costPerformanceIndex()).isEqualByComparingTo("1.5000");
            assertThat(result.schedulePerformanceIndex()).isNull();
            assertThat(result.schedulePerformanceInterpretation())
                    .isEqualTo(SchedulePerformanceStatus.NOT_AVAILABLE.getInterpretation());
        }

        @Test
        @DisplayName("Actual progress = 0: EV = 0, CPI = 0")
        void shouldReturnZeroEvWhenActualProgressIsZero() {
            Activity activity = buildActivity("10000", "50", "0", "2000");

            EvmIndicatorsResponse result = calculator.calculateForActivity(activity);

            assertThat(result.earnedValue()).isEqualByComparingTo("0.00");
            assertThat(result.costVariance()).isEqualByComparingTo("-2000.00");
            assertThat(result.scheduleVariance()).isEqualByComparingTo("-5000.00");
            assertThat(result.costPerformanceIndex()).isEqualByComparingTo("0.0000");
            assertThat(result.schedulePerformanceIndex()).isEqualByComparingTo("0.0000");
            assertThat(result.estimateAtCompletion()).isNull();
            assertThat(result.costPerformanceInterpretation())
                    .isEqualTo(CostPerformanceStatus.OVER_BUDGET.getInterpretation());
            assertThat(result.schedulePerformanceInterpretation())
                    .isEqualTo(SchedulePerformanceStatus.BEHIND_SCHEDULE.getInterpretation());
        }

        @Test
        @DisplayName("Both AC and actual progress = 0: CPI null (0/0)")
        void shouldHandleBothAcAndActualProgressBeingZero() {
            Activity activity = buildActivity("10000", "50", "0", "0");

            EvmIndicatorsResponse result = calculator.calculateForActivity(activity);

            assertThat(result.earnedValue()).isEqualByComparingTo("0.00");
            assertThat(result.costVariance()).isEqualByComparingTo("0.00");
            assertThat(result.costPerformanceIndex()).isNull();
            assertThat(result.estimateAtCompletion()).isNull();
            assertThat(result.varianceAtCompletion()).isNull();
        }

        @Test
        @DisplayName("All zeros: BAC=0, progress=0, AC=0")
        void shouldHandleAllZeroValues() {
            Activity activity = buildActivity("0", "0", "0", "0");

            EvmIndicatorsResponse result = calculator.calculateForActivity(activity);

            assertThat(result.plannedValue()).isEqualByComparingTo("0.00");
            assertThat(result.earnedValue()).isEqualByComparingTo("0.00");
            assertThat(result.costVariance()).isEqualByComparingTo("0.00");
            assertThat(result.scheduleVariance()).isEqualByComparingTo("0.00");
            assertThat(result.costPerformanceIndex()).isNull();
            assertThat(result.schedulePerformanceIndex()).isNull();
        }
    }

    @Nested
    @DisplayName("Consolidated calculations")
    class ConsolidatedCalculations {

        @Test
        @DisplayName("Empty activity list returns zero indicators")
        void shouldReturnEmptyIndicatorsForNoActivities() {
            EvmIndicatorsResponse result = calculator.calculateConsolidated(Collections.emptyList());

            assertThat(result.plannedValue()).isEqualByComparingTo("0");
            assertThat(result.earnedValue()).isEqualByComparingTo("0");
            assertThat(result.costVariance()).isEqualByComparingTo("0");
            assertThat(result.scheduleVariance()).isEqualByComparingTo("0");
            assertThat(result.costPerformanceIndex()).isNull();
            assertThat(result.schedulePerformanceIndex()).isNull();
            assertThat(result.estimateAtCompletion()).isNull();
            assertThat(result.varianceAtCompletion()).isNull();
            assertThat(result.costPerformanceInterpretation())
                    .isEqualTo(CostPerformanceStatus.NOT_AVAILABLE.getInterpretation());
            assertThat(result.schedulePerformanceInterpretation())
                    .isEqualTo(SchedulePerformanceStatus.NOT_AVAILABLE.getInterpretation());
        }

        @Test
        @DisplayName("Consolidates multiple activities by summing PV, EV, AC, BAC")
        void shouldConsolidateMultipleActivities() {
            Activity a1 = buildActivity("10000", "50", "40", "5000");
            Activity a2 = buildActivity("20000", "60", "70", "10000");

            EvmIndicatorsResponse result = calculator.calculateConsolidated(List.of(a1, a2));

            // a1: PV=5000, EV=4000 | a2: PV=12000, EV=14000
            // Totals: PV=17000, EV=18000, AC=15000, BAC=30000
            assertThat(result.plannedValue()).isEqualByComparingTo("17000.00");
            assertThat(result.earnedValue()).isEqualByComparingTo("18000.00");
            assertThat(result.costVariance()).isEqualByComparingTo("3000.00");
            assertThat(result.scheduleVariance()).isEqualByComparingTo("1000.00");
            assertThat(result.costPerformanceIndex()).isEqualByComparingTo("1.2000");
            assertThat(result.estimateAtCompletion()).isEqualByComparingTo("25000.00");
            assertThat(result.varianceAtCompletion()).isEqualByComparingTo("5000.00");
            assertThat(result.costPerformanceInterpretation())
                    .isEqualTo(CostPerformanceStatus.UNDER_BUDGET.getInterpretation());
            assertThat(result.schedulePerformanceInterpretation())
                    .isEqualTo(SchedulePerformanceStatus.AHEAD_OF_SCHEDULE.getInterpretation());
        }

        @Test
        @DisplayName("Single activity consolidated matches individual calculation")
        void shouldMatchIndividualCalculationForSingleActivity() {
            Activity activity = buildActivity("10000", "50", "40", "5000");

            EvmIndicatorsResponse individual = calculator.calculateForActivity(activity);
            EvmIndicatorsResponse consolidated = calculator.calculateConsolidated(List.of(activity));

            assertThat(consolidated.plannedValue()).isEqualByComparingTo(individual.plannedValue());
            assertThat(consolidated.earnedValue()).isEqualByComparingTo(individual.earnedValue());
            assertThat(consolidated.costVariance()).isEqualByComparingTo(individual.costVariance());
            assertThat(consolidated.scheduleVariance()).isEqualByComparingTo(individual.scheduleVariance());
            assertThat(consolidated.costPerformanceIndex()).isEqualByComparingTo(individual.costPerformanceIndex());
            assertThat(consolidated.schedulePerformanceIndex()).isEqualByComparingTo(individual.schedulePerformanceIndex());
        }

        @Test
        @DisplayName("Consolidated with all activities having AC=0")
        void shouldHandleConsolidatedWhenAllActivitiesHaveZeroAc() {
            Activity a1 = buildActivity("5000", "50", "40", "0");
            Activity a2 = buildActivity("5000", "60", "30", "0");

            EvmIndicatorsResponse result = calculator.calculateConsolidated(List.of(a1, a2));

            assertThat(result.costPerformanceIndex()).isNull();
            assertThat(result.estimateAtCompletion()).isNull();
            assertThat(result.costPerformanceInterpretation())
                    .isEqualTo(CostPerformanceStatus.NOT_AVAILABLE.getInterpretation());
        }
    }

    private Activity buildActivity(String bac, String planned, String actual, String ac) {
        Activity activity = new Activity();
        activity.setName("Test Activity");
        activity.setBudgetAtCompletion(new BigDecimal(bac));
        activity.setPlannedProgress(new BigDecimal(planned));
        activity.setActualProgress(new BigDecimal(actual));
        activity.setActualCost(new BigDecimal(ac));
        return activity;
    }
}
