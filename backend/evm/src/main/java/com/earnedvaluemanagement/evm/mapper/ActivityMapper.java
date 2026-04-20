package com.earnedvaluemanagement.evm.mapper;

import com.earnedvaluemanagement.evm.dto.request.ActivityRequest;
import com.earnedvaluemanagement.evm.dto.response.ActivityResponse;
import com.earnedvaluemanagement.evm.dto.response.EvmIndicatorsResponse;
import com.earnedvaluemanagement.evm.entity.Activity;
import com.earnedvaluemanagement.evm.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {

    public Activity toEntity(ActivityRequest request, Project project) {
        Activity activity = new Activity();
        activity.setName(request.name());
        activity.setBudgetAtCompletion(request.budgetAtCompletion());
        activity.setPlannedProgress(request.plannedProgress());
        activity.setActualProgress(request.actualProgress());
        activity.setActualCost(request.actualCost());
        activity.setProject(project);
        return activity;
    }

    public ActivityResponse toResponse(Activity activity, EvmIndicatorsResponse indicators) {
        return new ActivityResponse(
                activity.getId(),
                activity.getName(),
                activity.getBudgetAtCompletion(),
                activity.getPlannedProgress(),
                activity.getActualProgress(),
                activity.getActualCost(),
                indicators
        );
    }

    public void updateEntity(Activity activity, ActivityRequest request) {
        activity.setName(request.name());
        activity.setBudgetAtCompletion(request.budgetAtCompletion());
        activity.setPlannedProgress(request.plannedProgress());
        activity.setActualProgress(request.actualProgress());
        activity.setActualCost(request.actualCost());
    }
}
