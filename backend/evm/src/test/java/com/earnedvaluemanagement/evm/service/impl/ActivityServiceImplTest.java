package com.earnedvaluemanagement.evm.service.impl;

import com.earnedvaluemanagement.evm.dto.request.ActivityRequest;
import com.earnedvaluemanagement.evm.dto.response.ActivityResponse;
import com.earnedvaluemanagement.evm.dto.response.EvmIndicatorsResponse;
import com.earnedvaluemanagement.evm.entity.Activity;
import com.earnedvaluemanagement.evm.entity.Project;
import com.earnedvaluemanagement.evm.exception.ResourceNotFoundException;
import com.earnedvaluemanagement.evm.mapper.ActivityMapper;
import com.earnedvaluemanagement.evm.repository.ActivityRepository;
import com.earnedvaluemanagement.evm.repository.ProjectRepository;
import com.earnedvaluemanagement.evm.service.EvmCalculationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityServiceImplTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ActivityMapper activityMapper;

    @Mock
    private EvmCalculationService evmCalculationService;

    @InjectMocks
    private ActivityServiceImpl activityService;

    private static final Long PROJECT_ID = 1L;
    private static final Long ACTIVITY_ID = 10L;

    @Test
    @DisplayName("findAllByProjectId returns activities with EVM indicators")
    void shouldReturnAllActivitiesForProject() {
        Activity activity = buildActivity();
        EvmIndicatorsResponse indicators = buildIndicators();
        ActivityResponse response = buildActivityResponse(indicators);

        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(activityRepository.findByProjectId(PROJECT_ID)).thenReturn(List.of(activity));
        when(evmCalculationService.calculateForActivity(activity)).thenReturn(indicators);
        when(activityMapper.toResponse(activity, indicators)).thenReturn(response);

        List<ActivityResponse> result = activityService.findAllByProjectId(PROJECT_ID);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().indicators()).isNotNull();
    }

    @Test
    @DisplayName("findById returns single activity with indicators")
    void shouldReturnActivityWithIndicators() {
        Activity activity = buildActivity();
        EvmIndicatorsResponse indicators = buildIndicators();
        ActivityResponse response = buildActivityResponse(indicators);

        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(activityRepository.findByIdAndProjectId(ACTIVITY_ID, PROJECT_ID)).thenReturn(Optional.of(activity));
        when(evmCalculationService.calculateForActivity(activity)).thenReturn(indicators);
        when(activityMapper.toResponse(activity, indicators)).thenReturn(response);

        ActivityResponse result = activityService.findById(PROJECT_ID, ACTIVITY_ID);

        assertThat(result.name()).isEqualTo("Design Phase");
        assertThat(result.indicators()).isNotNull();
    }

    @Test
    @DisplayName("create persists activity and returns response with indicators")
    void shouldCreateActivity() {
        ActivityRequest request = new ActivityRequest(
                "Design Phase", new BigDecimal("10000"),
                new BigDecimal("50"), new BigDecimal("40"), new BigDecimal("5000"));
        Project project = buildProject();
        Activity activity = buildActivity();
        EvmIndicatorsResponse indicators = buildIndicators();
        ActivityResponse response = buildActivityResponse(indicators);

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(activityMapper.toEntity(request, project)).thenReturn(activity);
        when(activityRepository.save(activity)).thenReturn(activity);
        when(evmCalculationService.calculateForActivity(activity)).thenReturn(indicators);
        when(activityMapper.toResponse(activity, indicators)).thenReturn(response);

        ActivityResponse result = activityService.create(PROJECT_ID, request);

        assertThat(result.name()).isEqualTo("Design Phase");
        verify(activityRepository).save(activity);
    }

    @Test
    @DisplayName("update modifies activity and recalculates indicators")
    void shouldUpdateActivity() {
        ActivityRequest request = new ActivityRequest(
                "Updated", new BigDecimal("15000"),
                new BigDecimal("60"), new BigDecimal("50"), new BigDecimal("7000"));
        Activity activity = buildActivity();
        EvmIndicatorsResponse indicators = buildIndicators();
        ActivityResponse response = buildActivityResponse(indicators);

        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(activityRepository.findByIdAndProjectId(ACTIVITY_ID, PROJECT_ID)).thenReturn(Optional.of(activity));
        when(activityRepository.save(activity)).thenReturn(activity);
        when(evmCalculationService.calculateForActivity(activity)).thenReturn(indicators);
        when(activityMapper.toResponse(activity, indicators)).thenReturn(response);

        ActivityResponse result = activityService.update(PROJECT_ID, ACTIVITY_ID, request);

        assertThat(result).isNotNull();
        verify(activityMapper).updateEntity(activity, request);
        verify(activityRepository).save(activity);
    }

    @Test
    @DisplayName("delete removes existing activity")
    void shouldDeleteActivity() {
        Activity activity = buildActivity();

        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(activityRepository.findByIdAndProjectId(ACTIVITY_ID, PROJECT_ID)).thenReturn(Optional.of(activity));

        activityService.delete(PROJECT_ID, ACTIVITY_ID);

        verify(activityRepository).delete(activity);
    }

    @Test
    @DisplayName("throws ResourceNotFoundException when project does not exist")
    void shouldThrowWhenProjectNotFound() {
        when(projectRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> activityService.findAllByProjectId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Project");
    }

    @Test
    @DisplayName("throws ResourceNotFoundException when activity does not exist")
    void shouldThrowWhenActivityNotFound() {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(activityRepository.findByIdAndProjectId(999L, PROJECT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityService.findById(PROJECT_ID, 999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Activity");
    }

    private Project buildProject() {
        Project project = new Project();
        project.setId(PROJECT_ID);
        project.setName("Test Project");
        return project;
    }

    private Activity buildActivity() {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_ID);
        activity.setName("Design Phase");
        activity.setBudgetAtCompletion(new BigDecimal("10000"));
        activity.setPlannedProgress(new BigDecimal("50"));
        activity.setActualProgress(new BigDecimal("40"));
        activity.setActualCost(new BigDecimal("5000"));
        activity.setProject(buildProject());
        return activity;
    }

    private EvmIndicatorsResponse buildIndicators() {
        return new EvmIndicatorsResponse(
                new BigDecimal("5000"), new BigDecimal("4000"),
                new BigDecimal("-1000"), new BigDecimal("-1000"),
                new BigDecimal("0.8"), new BigDecimal("0.8"),
                new BigDecimal("12500"), new BigDecimal("-2500"),
                "Over budget", "Behind schedule"
        );
    }

    private ActivityResponse buildActivityResponse(EvmIndicatorsResponse indicators) {
        return new ActivityResponse(
                ACTIVITY_ID, "Design Phase", new BigDecimal("10000"),
                new BigDecimal("50"), new BigDecimal("40"),
                new BigDecimal("5000"), indicators
        );
    }
}
