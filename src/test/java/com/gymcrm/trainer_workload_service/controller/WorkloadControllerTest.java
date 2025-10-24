package com.gymcrm.trainer_workload_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gymcrm.trainer_workload_service.dto.MonthlyWorkloadResponse;
import com.gymcrm.trainer_workload_service.dto.WorkloadRequest;
import com.gymcrm.trainer_workload_service.model.TrainerWorkload;
import com.gymcrm.trainer_workload_service.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(WorkloadController.class)
class WorkloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerWorkloadService workloadService;

    private ObjectMapper objectMapper;
    private WorkloadRequest validRequest;
    private TrainerWorkload trainerWorkload;
    private MonthlyWorkloadResponse monthlyResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        validRequest = new WorkloadRequest();
        validRequest.setTrainerUsername("john.doe");
        validRequest.setTrainerFirstName("John");
        validRequest.setTrainerLastName("Doe");
        validRequest.setIsActive(true);
        validRequest.setTrainingDate(LocalDate.of(2024, 5, 15));
        validRequest.setTrainingDuration(60);
        validRequest.setActionType(WorkloadRequest.ActionType.ADD);

        trainerWorkload = new TrainerWorkload();
        trainerWorkload.setTrainerUsername("john.doe");
        trainerWorkload.setTrainerFirstName("John");
        trainerWorkload.setTrainerLastName("Doe");
        trainerWorkload.setTrainerStatus(true);

        monthlyResponse = new MonthlyWorkloadResponse(2024, 5, 60);
    }

    @Test
    @WithMockUser
    void processWorkload_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        mockMvc.perform(post("/api/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void processWorkload_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
        doThrow(new IllegalArgumentException("Invalid request")).when(workloadService).processWorkload(any(WorkloadRequest.class));

        mockMvc.perform(post("/api/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getTrainerWorkload_ShouldReturnWorkload_WhenTrainerExists() throws Exception {
        when(workloadService.getTrainerWorkload("john.doe")).thenReturn(trainerWorkload);

        mockMvc.perform(get("/api/workload/john.doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value("john.doe"))
                .andExpect(jsonPath("$.trainerFirstName").value("John"))
                .andExpect(jsonPath("$.trainerLastName").value("Doe"))
                .andExpect(jsonPath("$.trainerStatus").value(true));
    }

    @Test
    @WithMockUser
    void getTrainerWorkload_ShouldReturnNotFound_WhenTrainerNotExists() throws Exception {
        when(workloadService.getTrainerWorkload("nonexistent")).thenThrow(new IllegalArgumentException("Trainer not found"));

        mockMvc.perform(get("/api/workload/nonexistent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getMonthlyWorkload_ShouldReturnWorkload() throws Exception {
        when(workloadService.getMonthlyWorkload(eq("john.doe"), eq(2024), eq(5))).thenReturn(monthlyResponse);

        mockMvc.perform(get("/api/workload/john.doe/2024/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.month").value(5))
                .andExpect(jsonPath("$.hours").value(60));
    }

    @Test
    @WithMockUser
    void getAllWorkloads_ShouldReturnAllWorkloads() throws Exception {
        List<TrainerWorkload> workloads = Arrays.asList(trainerWorkload);
        when(workloadService.getAllTrainerWorkloads()).thenReturn(workloads);

        mockMvc.perform(get("/api/workload")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].trainerUsername").value("john.doe"));
    }
}