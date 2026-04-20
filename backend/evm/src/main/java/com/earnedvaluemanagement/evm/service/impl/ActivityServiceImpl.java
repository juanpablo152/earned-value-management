package com.earnedvaluemanagement.evm.service.impl;

import com.earnedvaluemanagement.evm.dto.request.ActivityRequest;
import com.earnedvaluemanagement.evm.dto.response.ActivityResponse;
import com.earnedvaluemanagement.evm.entity.Activity;
import com.earnedvaluemanagement.evm.entity.Project;
import com.earnedvaluemanagement.evm.exception.ResourceNotFoundException;
import com.earnedvaluemanagement.evm.mapper.ActivityMapper;
import com.earnedvaluemanagement.evm.repository.ActivityRepository;
import com.earnedvaluemanagement.evm.repository.ProjectRepository;
import com.earnedvaluemanagement.evm.service.ActivityService;
import com.earnedvaluemanagement.evm.service.EvmCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final ProjectRepository projectRepository;
    private final ActivityMapper activityMapper;
    private final EvmCalculationService evmCalculationService;

    @Override
    public List<ActivityResponse> findAllByProjectId(Long projectId) {
        verifyProjectExists(projectId);
        return activityRepository.findByProjectId(projectId).stream()
                .map(this::toResponseWithIndicators)
                .toList();
    }

    @Override
    public ActivityResponse findById(Long projectId, Long activityId) {
        Activity activity = findActivityOrThrow(projectId, activityId);
        return toResponseWithIndicators(activity);
    }

    @Override
    @Transactional
    public ActivityResponse create(Long projectId, ActivityRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        Activity activity = activityMapper.toEntity(request, project);
        Activity saved = activityRepository.save(activity);
        return toResponseWithIndicators(saved);
    }

    @Override
    @Transactional
    public ActivityResponse update(Long projectId, Long activityId, ActivityRequest request) {
        Activity activity = findActivityOrThrow(projectId, activityId);
        activityMapper.updateEntity(activity, request);
        Activity updated = activityRepository.save(activity);
        return toResponseWithIndicators(updated);
    }

    @Override
    @Transactional
    public void delete(Long projectId, Long activityId) {
        Activity activity = findActivityOrThrow(projectId, activityId);
        activityRepository.delete(activity);
    }

    private ActivityResponse toResponseWithIndicators(Activity activity) {
        return activityMapper.toResponse(
                activity,
                evmCalculationService.calculateForActivity(activity));
    }

    private Activity findActivityOrThrow(Long projectId, Long activityId) {
        verifyProjectExists(projectId);
        return activityRepository.findByIdAndProjectId(activityId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", activityId));
    }

    private void verifyProjectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project", projectId);
        }
    }
}
