package com.earnedvaluemanagement.evm.service;

import com.earnedvaluemanagement.evm.dto.request.ProjectRequest;
import com.earnedvaluemanagement.evm.dto.response.ProjectDetailResponse;
import com.earnedvaluemanagement.evm.dto.response.ProjectResponse;

import java.util.List;

public interface ProjectService {

    List<ProjectResponse> findAll();

    ProjectDetailResponse findById(Long id);

    ProjectResponse create(ProjectRequest request);

    ProjectResponse update(Long id, ProjectRequest request);

    void delete(Long id);
}
