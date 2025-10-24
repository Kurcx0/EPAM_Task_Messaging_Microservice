package com.gymcrm.trainer_workload_service.service.impl;

import com.gymcrm.trainer_workload_service.dto.MonthlyWorkloadResponse;
import com.gymcrm.trainer_workload_service.dto.WorkloadRequest;
import com.gymcrm.trainer_workload_service.model.TrainerWorkload;
import com.gymcrm.trainer_workload_service.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceImplTest {

    @Mock
    private TrainerWorkloadRepository repository;

    @InjectMocks
    private TrainerWorkloadServiceImpl service;

    private WorkloadRequest addRequest;
    private WorkloadRequest deleteRequest;
    private TrainerWorkload existingWorkload;
    private final String testUsername = "john.doe";

    @BeforeEach
    void setUp() {
        LocalDate trainingDate = LocalDate.of(2024, 5, 15);

        addRequest = new WorkloadRequest();
        addRequest.setTrainerUsername(testUsername);
        addRequest.setTrainerFirstName("John");
        addRequest.setTrainerLastName("Doe");
        addRequest.setIsActive(true);
        addRequest.setTrainingDate(trainingDate);
        addRequest.setTrainingDuration(60);
        addRequest.setActionType(WorkloadRequest.ActionType.ADD);

        deleteRequest = new WorkloadRequest();
        deleteRequest.setTrainerUsername(testUsername);
        deleteRequest.setTrainerFirstName("John");
        deleteRequest.setTrainerLastName("Doe");
        deleteRequest.setIsActive(true);
        deleteRequest.setTrainingDate(trainingDate);
        deleteRequest.setTrainingDuration(30);
        deleteRequest.setActionType(WorkloadRequest.ActionType.DELETE);

        existingWorkload = new TrainerWorkload();
        existingWorkload.setTrainerUsername(testUsername);
        existingWorkload.setTrainerFirstName("John");
        existingWorkload.setTrainerLastName("Doe");
        existingWorkload.setTrainerStatus(true);
    }

    @Test
    void processWorkload_ShouldCreateNewWorkload_WhenTrainerNotExists() {
        when(repository.findByUsername(testUsername)).thenReturn(Optional.empty());
        when(repository.save(any(TrainerWorkload.class))).thenAnswer(i -> i.getArgument(0));

        service.processWorkload(addRequest);

        verify(repository).findByUsername(testUsername);
        verify(repository).save(any(TrainerWorkload.class));
    }

    @Test
    void processWorkload_ShouldUpdateExistingWorkload_WhenTrainerExists() {
        when(repository.findByUsername(testUsername)).thenReturn(Optional.of(existingWorkload));
        when(repository.save(any(TrainerWorkload.class))).thenAnswer(i -> i.getArgument(0));

        service.processWorkload(addRequest);

        verify(repository).findByUsername(testUsername);
        verify(repository).save(existingWorkload);

        Integer workloadValue = existingWorkload.getMonthlyWorkload(2024, 5);
        assertEquals(60, workloadValue);
    }

    @Test
    void processWorkload_ShouldDecreaseDuration_WhenActionTypeIsDelete() {
        when(repository.findByUsername(testUsername)).thenReturn(Optional.of(existingWorkload));
        when(repository.save(any(TrainerWorkload.class))).thenAnswer(i -> i.getArgument(0));

        service.processWorkload(addRequest);
        service.processWorkload(deleteRequest);

        verify(repository, times(2)).findByUsername(testUsername);
        verify(repository, times(2)).save(existingWorkload);

        Integer workloadValue = existingWorkload.getMonthlyWorkload(2024, 5);
        assertEquals(30, workloadValue);
    }

    @Test
    void processWorkload_ShouldThrowException_WhenRequestIsInvalid() {
        WorkloadRequest invalidRequest = new WorkloadRequest();
        invalidRequest.setTrainerUsername(testUsername);

        assertThrows(IllegalArgumentException.class, () -> service.processWorkload(invalidRequest));
    }

    @Test
    void getTrainerWorkload_ShouldReturnWorkload_WhenTrainerExists() {
        when(repository.findByUsername(testUsername)).thenReturn(Optional.of(existingWorkload));

        TrainerWorkload result = service.getTrainerWorkload(testUsername);

        assertNotNull(result);
        assertEquals(testUsername, result.getTrainerUsername());
        verify(repository).findByUsername(testUsername);
    }

    @Test
    void getTrainerWorkload_ShouldThrowException_WhenTrainerNotExists() {
        when(repository.findByUsername(testUsername)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.getTrainerWorkload(testUsername));
        verify(repository).findByUsername(testUsername);
    }

    @Test
    void getAllTrainerWorkloads_ShouldReturnAllWorkloads() {
        List<TrainerWorkload> expectedWorkloads = Arrays.asList(existingWorkload);
        when(repository.findAll()).thenReturn(expectedWorkloads);

        List<TrainerWorkload> result = service.getAllTrainerWorkloads();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUsername, result.get(0).getTrainerUsername());
        verify(repository).findAll();
    }

    @Test
    void getMonthlyWorkload_ShouldReturnZero_WhenTrainerNotExists() {
        when(repository.findByUsername(testUsername)).thenReturn(Optional.empty());

        MonthlyWorkloadResponse result = service.getMonthlyWorkload(testUsername, 2024, 5);

        assertNotNull(result);
        assertEquals(0, result.getHours());
        assertEquals(2024, result.getYear());
        assertEquals(5, result.getMonth());
        verify(repository).findByUsername(testUsername);
    }

    @Test
    void getMonthlyWorkload_ShouldReturnWorkload_WhenTrainerExists() {
        when(repository.findByUsername(testUsername)).thenReturn(Optional.of(existingWorkload));
        when(repository.save(any(TrainerWorkload.class))).thenAnswer(i -> i.getArgument(0));

        service.processWorkload(addRequest);
        MonthlyWorkloadResponse result = service.getMonthlyWorkload(testUsername, 2024, 5);

        assertNotNull(result);
        assertEquals(60, result.getHours());
        assertEquals(2024, result.getYear());
        assertEquals(5, result.getMonth());
    }

    @Test
    void getMonthlyWorkload_ShouldThrowException_WhenParametersAreInvalid() {
        assertThrows(IllegalArgumentException.class, () -> service.getMonthlyWorkload(null, 2024, 5));
        assertThrows(IllegalArgumentException.class, () -> service.getMonthlyWorkload(testUsername, null, 5));
        assertThrows(IllegalArgumentException.class, () -> service.getMonthlyWorkload(testUsername, 2024, null));
    }
}