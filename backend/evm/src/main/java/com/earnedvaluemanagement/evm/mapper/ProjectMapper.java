package com.earnedvaluemanagement.evm.mapper;

import com.earnedvaluemanagement.evm.dto.request.ProjectRequest;
import com.earnedvaluemanagement.evm.dto.response.ActivityResponse;
import com.earnedvaluemanagement.evm.dto.response.EvmIndicatorsResponse;
import com.earnedvaluemanagement.evm.dto.response.ProjectDetailResponse;
import com.earnedvaluemanagement.evm.dto.response.ProjectResponse;
import com.earnedvaluemanagement.evm.entity.Project;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectMapper {

    public Project toEntity(ProjectRequest request) {
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        return project;
    }

    public ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    public ProjectDetailResponse toDetailResponse(
            Project project,
            List<ActivityResponse> activities,
            EvmIndicatorsResponse consolidatedIndicators) {
        return new ProjectDetailResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getUpdatedAt(),
                activities,
                consolidatedIndicators
        );
    }

    public void updateEntity(Project project, ProjectRequest request) {
        project.setName(request.name());
        project.setDescription(request.description());
    }
}
