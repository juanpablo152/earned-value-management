package com.earnedvaluemanagement.evm.service;

import com.earnedvaluemanagement.evm.dto.request.ActivityRequest;
import com.earnedvaluemanagement.evm.dto.response.ActivityResponse;

import java.util.List;

public interface ActivityService {

    List<ActivityResponse> findAllByProjectId(Long projectId);

    ActivityResponse findById(Long projectId, Long activityId);

    ActivityResponse create(Long projectId, ActivityRequest request);

    ActivityResponse update(Long projectId, Long activityId, ActivityRequest request);

    void delete(Long projectId, Long activityId);
}
