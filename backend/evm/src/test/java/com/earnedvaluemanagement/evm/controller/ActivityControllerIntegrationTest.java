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
class ActivityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ActivityRepository activityRepository;

    private Project testProject;

    @BeforeEach
    void setUp() {
        activityRepository.deleteAll();
        projectRepository.deleteAll();

        testProject = new Project();
        testProject.setName("Test Project");
        testProject = projectRepository.save(testProject);
    }

    @Test
    @DisplayName("POST /api/projects/{pid}/activities - creates activity with EVM indicators")
    void shouldCreateActivityWithIndicators() throws Exception {
        String body = """
                {
                    "name": "Design Phase",
                    "budgetAtCompletion": 10000,
                    "plannedProgress": 50,
                    "actualProgress": 40,
                    "actualCost": 5000
                }
                """;

        mockMvc.perform(post("/api/projects/{pid}/activities", testProject.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Design Phase"))
                .andExpect(jsonPath("$.budgetAtCompletion").isNumber())
                .andExpect(jsonPath("$.indicators").exists())
                .andExpect(jsonPath("$.indicators.plannedValue").value(5000.0))
                .andExpect(jsonPath("$.indicators.earnedValue").value(4000.0))
                .andExpect(jsonPath("$.indicators.costVariance").value(-1000.0))
                .andExpect(jsonPath("$.indicators.scheduleVariance").value(-1000.0))
                .andExpect(jsonPath("$.indicators.costPerformanceInterpretation").isString())
                .andExpect(jsonPath("$.indicators.schedulePerformanceInterpretation").isString());
    }

    @Test
    @DisplayName("GET /api/projects/{pid}/activities - lists activities with indicators")
    void shouldListActivitiesWithIndicators() throws Exception {
        persistActivity("Activity A", "10000", "50", "40", "5000");
        persistActivity("Activity B", "20000", "70", "60", "15000");

        mockMvc.perform(get("/api/projects/{pid}/activities", testProject.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].indicators").exists())
                .andExpect(jsonPath("$[1].indicators").exists());
    }

    @Test
    @DisplayName("GET /api/projects/{pid}/activities/{id} - returns activity with indicators")
    void shouldReturnActivityDetail() throws Exception {
        Activity activity = persistActivity("Design", "10000", "50", "50", "5000");

        mockMvc.perform(get("/api/projects/{pid}/activities/{id}",
                        testProject.getId(), activity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Design"))
                .andExpect(jsonPath("$.indicators.plannedValue").value(5000.0))
                .andExpect(jsonPath("$.indicators.earnedValue").value(5000.0))
                .andExpect(jsonPath("$.indicators.costVariance").value(0.0))
                .andExpect(jsonPath("$.indicators.scheduleVariance").value(0.0));
    }

    @Test
    @DisplayName("PUT /api/projects/{pid}/activities/{id} - updates and recalculates indicators")
    void shouldUpdateActivityAndRecalculateIndicators() throws Exception {
        Activity activity = persistActivity("Design", "10000", "50", "40", "5000");

        String body = """
                {
                    "name": "Design Phase Updated",
                    "budgetAtCompletion": 10000,
                    "plannedProgress": 50,
                    "actualProgress": 60,
                    "actualCost": 4000
                }
                """;

        mockMvc.perform(put("/api/projects/{pid}/activities/{id}",
                        testProject.getId(), activity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Design Phase Updated"))
                .andExpect(jsonPath("$.indicators.earnedValue").value(6000.0))
                .andExpect(jsonPath("$.indicators.costVariance").value(2000.0));
    }

    @Test
    @DisplayName("DELETE /api/projects/{pid}/activities/{id} - returns 204")
    void shouldDeleteActivity() throws Exception {
        Activity activity = persistActivity("To Delete", "5000", "50", "50", "5000");

        mockMvc.perform(delete("/api/projects/{pid}/activities/{id}",
                        testProject.getId(), activity.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/projects/{pid}/activities/{id}",
                        testProject.getId(), activity.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST - returns 404 when project does not exist")
    void shouldReturn404WhenProjectNotFound() throws Exception {
        String body = """
                {
                    "name": "Orphan",
                    "budgetAtCompletion": 1000,
                    "plannedProgress": 50,
                    "actualProgress": 50,
                    "actualCost": 500
                }
                """;

        mockMvc.perform(post("/api/projects/{pid}/activities", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("GET - returns 404 for non-existent activity")
    void shouldReturn404ForNonExistentActivity() throws Exception {
        mockMvc.perform(get("/api/projects/{pid}/activities/{id}",
                        testProject.getId(), 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST - returns 400 for invalid activity data")
    void shouldReturn400ForInvalidActivity() throws Exception {
        String body = """
                {
                    "name": "",
                    "budgetAtCompletion": -100,
                    "plannedProgress": 150,
                    "actualProgress": -10,
                    "actualCost": -500
                }
                """;

        mockMvc.perform(post("/api/projects/{pid}/activities", testProject.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    @DisplayName("POST - correctly handles activity with AC=0 (edge case)")
    void shouldHandleActivityWithZeroActualCost() throws Exception {
        String body = """
                {
                    "name": "Zero AC",
                    "budgetAtCompletion": 10000,
                    "plannedProgress": 50,
                    "actualProgress": 30,
                    "actualCost": 0
                }
                """;

        mockMvc.perform(post("/api/projects/{pid}/activities", testProject.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.indicators.earnedValue").value(3000.0))
                .andExpect(jsonPath("$.indicators.costPerformanceIndex").doesNotExist())
                .andExpect(jsonPath("$.indicators.estimateAtCompletion").doesNotExist());
    }

    private Activity persistActivity(String name, String bac, String planned,
                                     String actual, String ac) {
        Activity activity = new Activity();
        activity.setName(name);
        activity.setBudgetAtCompletion(new BigDecimal(bac));
        activity.setPlannedProgress(new BigDecimal(planned));
        activity.setActualProgress(new BigDecimal(actual));
        activity.setActualCost(new BigDecimal(ac));
        activity.setProject(testProject);
        return activityRepository.save(activity);
    }
}
