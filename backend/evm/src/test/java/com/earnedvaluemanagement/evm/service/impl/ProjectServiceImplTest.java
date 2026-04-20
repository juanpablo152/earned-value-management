package com.earnedvaluemanagement.evm.service.impl;

import com.earnedvaluemanagement.evm.dto.request.ProjectRequest;
import com.earnedvaluemanagement.evm.dto.response.ActivityResponse;
import com.earnedvaluemanagement.evm.dto.response.EvmIndicatorsResponse;
import com.earnedvaluemanagement.evm.dto.response.ProjectDetailResponse;
import com.earnedvaluemanagement.evm.dto.response.ProjectResponse;
import com.earnedvaluemanagement.evm.entity.Activity;
import com.earnedvaluemanagement.evm.entity.Project;
import com.earnedvaluemanagement.evm.exception.ResourceNotFoundException;
import com.earnedvaluemanagement.evm.mapper.ActivityMapper;
import com.earnedvaluemanagement.evm.mapper.ProjectMapper;
import com.earnedvaluemanagement.evm.repository.ProjectRepository;
import com.earnedvaluemanagement.evm.service.EvmCalculationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ActivityMapper activityMapper;

    @Mock
    private EvmCalculationService evmCalculationService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    @DisplayName("findAll returns list of project responses")
    void shouldReturnAllProjects() {
        Project project = createProject(1L, "Project A");
        ProjectResponse response = new ProjectResponse(1L, "Project A", null, LocalDateTime.now(), LocalDateTime.now());

        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectMapper.toResponse(project)).thenReturn(response);

        List<ProjectResponse> result = projectService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Project A");
    }

    @Test
    @DisplayName("findById returns project detail with activities and consolidated indicators")
    void shouldReturnProjectDetailWithIndicators() {
        Project project = createProject(1L, "Project A");
        Activity activity = createActivity(project);
        project.getActivities().add(activity);

        EvmIndicatorsResponse activityIndicators = createIndicators();
        EvmIndicatorsResponse consolidated = createIndicators();
        ActivityResponse actResponse = new ActivityResponse(
                1L, "Activity 1", new BigDecimal("10000"), new BigDecimal("50"),
                new BigDecimal("40"), new BigDecimal("5000"), activityIndicators);
        ProjectDetailResponse detailResponse = new ProjectDetailResponse(
                1L, "Project A", null, LocalDateTime.now(), LocalDateTime.now(),
                List.of(actResponse), consolidated);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(evmCalculationService.calculateForActivity(activity)).thenReturn(activityIndicators);
        when(activityMapper.toResponse(activity, activityIndicators)).thenReturn(actResponse);
        when(evmCalculationService.calculateConsolidated(project.getActivities())).thenReturn(consolidated);
        when(projectMapper.toDetailResponse(eq(project), any(), eq(consolidated))).thenReturn(detailResponse);

        ProjectDetailResponse result = projectService.findById(1L);

        assertThat(result.activities()).hasSize(1);
        assertThat(result.consolidatedIndicators()).isNotNull();
    }

    @Test
    @DisplayName("create persists and returns project response")
    void shouldCreateProject() {
        ProjectRequest request = new ProjectRequest("New Project", "Description");
        Project project = createProject(null, "New Project");
        Project saved = createProject(1L, "New Project");
        ProjectResponse response = new ProjectResponse(1L, "New Project", "Description", LocalDateTime.now(), LocalDateTime.now());

        when(projectMapper.toEntity(request)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(saved);
        when(projectMapper.toResponse(saved)).thenReturn(response);

        ProjectResponse result = projectService.create(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("New Project");
        verify(projectRepository).save(project);
    }

    @Test
    @DisplayName("update modifies existing project")
    void shouldUpdateProject() {
        ProjectRequest request = new ProjectRequest("Updated", "New desc");
        Project existing = createProject(1L, "Old Name");
        ProjectResponse response = new ProjectResponse(1L, "Updated", "New desc", LocalDateTime.now(), LocalDateTime.now());

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(projectRepository.save(existing)).thenReturn(existing);
        when(projectMapper.toResponse(existing)).thenReturn(response);

        ProjectResponse result = projectService.update(1L, request);

        assertThat(result.name()).isEqualTo("Updated");
        verify(projectMapper).updateEntity(existing, request);
    }

    @Test
    @DisplayName("delete removes existing project")
    void shouldDeleteProject() {
        Project project = createProject(1L, "To Delete");
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.delete(1L);

        verify(projectRepository).delete(project);
    }

    @Test
    @DisplayName("findById throws ResourceNotFoundException for non-existent project")
    void shouldThrowWhenProjectNotFound() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Project")
                .hasMessageContaining("999");
    }

    private Project createProject(Long id, String name) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        return project;
    }

    private Activity createActivity(Project project) {
        Activity activity = new Activity();
        activity.setId(1L);
        activity.setName("Activity 1");
        activity.setBudgetAtCompletion(new BigDecimal("10000"));
        activity.setPlannedProgress(new BigDecimal("50"));
        activity.setActualProgress(new BigDecimal("40"));
        activity.setActualCost(new BigDecimal("5000"));
        activity.setProject(project);
        return activity;
    }

    private EvmIndicatorsResponse createIndicators() {
        return new EvmIndicatorsResponse(
                new BigDecimal("5000"), new BigDecimal("4000"),
                new BigDecimal("-1000"), new BigDecimal("-1000"),
                new BigDecimal("0.8"), new BigDecimal("0.8"),
                new BigDecimal("12500"), new BigDecimal("-2500"),
                "Over budget", "Behind schedule"
        );
    }
}
