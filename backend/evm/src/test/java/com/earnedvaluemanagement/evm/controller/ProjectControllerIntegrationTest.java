package com.earnedvaluemanagement.evm.controller;

import com.earnedvaluemanagement.evm.entity.Activity;
import com.earnedvaluemanagement.evm.entity.Project;
import com.earnedvaluemanagement.evm.repository.ActivityRepository;
import com.earnedvaluemanagement.evm.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @BeforeEach
    void setUp() {
        activityRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/projects - creates project and returns 201")
    void shouldCreateProject() throws Exception {
        String body = """
                {"name": "Construction Project", "description": "Highway renovation"}
                """;

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Construction Project"))
                .andExpect(jsonPath("$.description").value("Highway renovation"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("GET /api/projects - lists all projects")
    void shouldListProjects() throws Exception {
        persistProject("Project A");
        persistProject("Project B");

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists());
    }

    @Test
    @DisplayName("GET /api/projects/{id} - returns project detail with EVM indicators")
    void shouldReturnProjectDetailWithIndicators() throws Exception {
        Project project = persistProject("EVM Project");
        persistActivity(project, "Design", "10000", "50", "40", "5000");
        persistActivity(project, "Development", "20000", "60", "50", "12000");

        mockMvc.perform(get("/api/projects/{id}", project.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("EVM Project"))
                .andExpect(jsonPath("$.activities", hasSize(2)))
                .andExpect(jsonPath("$.activities[0].indicators").exists())
                .andExpect(jsonPath("$.activities[0].indicators.plannedValue").isNumber())
                .andExpect(jsonPath("$.activities[0].indicators.earnedValue").isNumber())
                .andExpect(jsonPath("$.activities[0].indicators.costVariance").isNumber())
                .andExpect(jsonPath("$.activities[0].indicators.scheduleVariance").isNumber())
                .andExpect(jsonPath("$.activities[0].indicators.costPerformanceInterpretation").isString())
                .andExpect(jsonPath("$.activities[0].indicators.schedulePerformanceInterpretation").isString())
                .andExpect(jsonPath("$.consolidatedIndicators").exists())
                .andExpect(jsonPath("$.consolidatedIndicators.plannedValue").isNumber())
                .andExpect(jsonPath("$.consolidatedIndicators.earnedValue").isNumber())
                .andExpect(jsonPath("$.consolidatedIndicators.costPerformanceIndex").isNumber())
                .andExpect(jsonPath("$.consolidatedIndicators.schedulePerformanceIndex").isNumber())
                .andExpect(jsonPath("$.consolidatedIndicators.estimateAtCompletion").isNumber())
                .andExpect(jsonPath("$.consolidatedIndicators.varianceAtCompletion").isNumber())
                .andExpect(jsonPath("$.consolidatedIndicators.costPerformanceInterpretation").isString());
    }

    @Test
    @DisplayName("GET /api/projects/{id} - empty project returns zero consolidated indicators")
    void shouldReturnEmptyConsolidatedForProjectWithNoActivities() throws Exception {
        Project project = persistProject("Empty Project");

        mockMvc.perform(get("/api/projects/{id}", project.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activities", hasSize(0)))
                .andExpect(jsonPath("$.consolidatedIndicators.plannedValue").value(0))
                .andExpect(jsonPath("$.consolidatedIndicators.earnedValue").value(0))
                .andExpect(jsonPath("$.consolidatedIndicators.costPerformanceIndex").doesNotExist())
                .andExpect(jsonPath("$.consolidatedIndicators.schedulePerformanceIndex").doesNotExist());
    }

    @Test
    @DisplayName("PUT /api/projects/{id} - updates project and returns 200")
    void shouldUpdateProject() throws Exception {
        Project project = persistProject("Original");

        String body = """
                {"name": "Updated Name", "description": "Updated description"}
                """;

        mockMvc.perform(put("/api/projects/{id}", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @DisplayName("DELETE /api/projects/{id} - deletes project and returns 204")
    void shouldDeleteProject() throws Exception {
        Project project = persistProject("To Delete");

        mockMvc.perform(delete("/api/projects/{id}", project.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/projects/{id}", project.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/projects/999 - returns 404 for non-existent project")
    void shouldReturn404ForNonExistentProject() throws Exception {
        mockMvc.perform(get("/api/projects/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /api/projects - returns 400 for invalid request")
    void shouldReturn400ForInvalidProject() throws Exception {
        String body = """
                {"name": "", "description": "Missing name"}
                """;

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    private Project persistProject(String name) {
        Project project = new Project();
        project.setName(name);
        return projectRepository.save(project);
    }

    private void persistActivity(Project project, String name, String bac,
                                 String planned, String actual, String ac) {
        Activity activity = new Activity();
        activity.setName(name);
        activity.setBudgetAtCompletion(new BigDecimal(bac));
        activity.setPlannedProgress(new BigDecimal(planned));
        activity.setActualProgress(new BigDecimal(actual));
        activity.setActualCost(new BigDecimal(ac));
        activity.setProject(project);
        activityRepository.save(activity);
    }
}
