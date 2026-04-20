package com.earnedvaluemanagement.evm.service.impl;

import com.earnedvaluemanagement.evm.dto.request.ProjectRequest;
import com.earnedvaluemanagement.evm.dto.response.ActivityResponse;
import com.earnedvaluemanagement.evm.dto.response.EvmIndicatorsResponse;
import com.earnedvaluemanagement.evm.dto.response.ProjectDetailResponse;
import com.earnedvaluemanagement.evm.dto.response.ProjectResponse;
import com.earnedvaluemanagement.evm.entity.Project;
import com.earnedvaluemanagement.evm.exception.ResourceNotFoundException;
import com.earnedvaluemanagement.evm.mapper.ActivityMapper;
import com.earnedvaluemanagement.evm.mapper.ProjectMapper;
import com.earnedvaluemanagement.evm.repository.ProjectRepository;
import com.earnedvaluemanagement.evm.service.EvmCalculationService;
import com.earnedvaluemanagement.evm.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ActivityMapper activityMapper;
    private final EvmCalculationService evmCalculationService;

    @Override
    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    @Override
    public ProjectDetailResponse findById(Long id) {
        Project project = findProjectOrThrow(id);

        List<ActivityResponse> activityResponses = project.getActivities().stream()
                .map(activity -> activityMapper.toResponse(
                        activity,
                        evmCalculationService.calculateForActivity(activity)))
                .toList();

        EvmIndicatorsResponse consolidated = evmCalculationService
                .calculateConsolidated(project.getActivities());

        return projectMapper.toDetailResponse(project, activityResponses, consolidated);
    }

    @Override
    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Project project = projectMapper.toEntity(request);
        Project saved = projectRepository.save(project);
        return projectMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = findProjectOrThrow(id);
        projectMapper.updateEntity(project, request);
        Project updated = projectRepository.save(project);
        return projectMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Project project = findProjectOrThrow(id);
        projectRepository.delete(project);
    }

    private Project findProjectOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    }
}
