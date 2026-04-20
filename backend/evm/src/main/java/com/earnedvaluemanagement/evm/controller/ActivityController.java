package com.earnedvaluemanagement.evm.controller;

import com.earnedvaluemanagement.evm.dto.request.ActivityRequest;
import com.earnedvaluemanagement.evm.dto.response.ActivityResponse;
import com.earnedvaluemanagement.evm.dto.response.ErrorResponse;
import com.earnedvaluemanagement.evm.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/activities")
@RequiredArgsConstructor
@Tag(name = "Activities", description = "Activity management and EVM indicator calculations")
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    @Operation(summary = "List all activities for a project with their EVM indicators")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activities retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ActivityResponse>> findAll(@PathVariable Long projectId) {
        return ResponseEntity.ok(activityService.findAllByProjectId(projectId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get activity details with EVM indicators")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activity retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Project or activity not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ActivityResponse> findById(
            @PathVariable Long projectId,
            @PathVariable Long id) {
        return ResponseEntity.ok(activityService.findById(projectId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new activity for a project")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Activity created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ActivityResponse> create(
            @PathVariable Long projectId,
            @Valid @RequestBody ActivityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(activityService.create(projectId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing activity")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activity updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project or activity not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ActivityResponse> update(
            @PathVariable Long projectId,
            @PathVariable Long id,
            @Valid @RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityService.update(projectId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an activity")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Activity deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Project or activity not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(
            @PathVariable Long projectId,
            @PathVariable Long id) {
        activityService.delete(projectId, id);
        return ResponseEntity.noContent().build();
    }
}
