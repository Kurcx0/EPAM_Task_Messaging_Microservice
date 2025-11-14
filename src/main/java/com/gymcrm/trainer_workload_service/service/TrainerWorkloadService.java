package com.gymcrm.trainer_workload_service.service;

import com.gymcrm.trainer_workload_service.dto.MonthlyWorkloadResponse;
import com.gymcrm.trainer_workload_service.dto.WorkloadRequest;
import com.gymcrm.trainer_workload_service.dto.messaging.TrainerWorkloadRequest;
import com.gymcrm.trainer_workload_service.dto.messaging.TrainerWorkloadResponse;
import com.gymcrm.trainer_workload_service.model.TrainerWorkload;

import java.util.List;

public interface TrainerWorkloadService {
    void processWorkload(WorkloadRequest request);
    TrainerWorkload getTrainerWorkload(String username);
    List<TrainerWorkload> getAllTrainerWorkloads();
    MonthlyWorkloadResponse getMonthlyWorkload(String username, Integer year, Integer month);
    TrainerWorkloadResponse calculateWorkload(TrainerWorkloadRequest request);
}