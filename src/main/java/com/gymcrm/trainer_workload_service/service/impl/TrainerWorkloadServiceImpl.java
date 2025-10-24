package com.gymcrm.trainer_workload_service.service.impl;

import com.gymcrm.trainer_workload_service.dto.MonthlyWorkloadResponse;
import com.gymcrm.trainer_workload_service.dto.WorkloadRequest;
import com.gymcrm.trainer_workload_service.model.MonthSummary;
import com.gymcrm.trainer_workload_service.model.TrainerWorkload;
import com.gymcrm.trainer_workload_service.model.YearSummary;
import com.gymcrm.trainer_workload_service.repository.TrainerWorkloadRepository;
import com.gymcrm.trainer_workload_service.service.TrainerWorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerWorkloadServiceImpl.class);
    private final TrainerWorkloadRepository repository;

    @Autowired
    public TrainerWorkloadServiceImpl(TrainerWorkloadRepository repository) {
        this.repository = repository;
    }

    @Override
    public void processWorkload(WorkloadRequest request) {
        logger.info("Processing workload request for trainer: {}", request.getTrainerUsername());

        if (request.getTrainerUsername() == null || request.getTrainingDate() == null ||
                request.getTrainingDuration() == null || request.getActionType() == null) {
            logger.error("Invalid workload request: missing required fields");
            throw new IllegalArgumentException("Trainer username, training date, duration, and action type are required");
        }

        TrainerWorkload trainerWorkload = repository.findByUsername(request.getTrainerUsername())
                .orElseGet(() -> createNewTrainerWorkload(request));

        updateTrainerInfo(trainerWorkload, request);

        int year = request.getTrainingDate().getYear();
        int month = request.getTrainingDate().getMonthValue();

        YearSummary yearSummary = trainerWorkload.getOrCreateYear(year);
        MonthSummary monthSummary = yearSummary.getOrCreateMonth(month);

        if (request.getActionType() == WorkloadRequest.ActionType.ADD) {
            logger.debug("Adding {} minutes to trainer {} workload for {}-{}",
                    request.getTrainingDuration(), request.getTrainerUsername(), year, month);
            monthSummary.addDuration(request.getTrainingDuration());
        } else if (request.getActionType() == WorkloadRequest.ActionType.DELETE) {
            logger.debug("Subtracting {} minutes from trainer {} workload for {}-{}",
                    request.getTrainingDuration(), request.getTrainerUsername(), year, month);
            monthSummary.subtractDuration(request.getTrainingDuration());
        }

        repository.save(trainerWorkload);
        logger.info("Workload processed successfully for trainer: {}", request.getTrainerUsername());
    }

    @Override
    public TrainerWorkload getTrainerWorkload(String username) {
        logger.info("Getting workload for trainer: {}", username);
        return repository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Trainer workload not found for username: {}", username);
                    return new IllegalArgumentException("Trainer workload not found for username: " + username);
                });
    }

    @Override
    public List<TrainerWorkload> getAllTrainerWorkloads() {
        logger.info("Getting all trainer workloads");
        return repository.findAll();
    }

    @Override
    public MonthlyWorkloadResponse getMonthlyWorkload(String username, Integer year, Integer month) {
        logger.info("Getting monthly workload for trainer: {}, year: {}, month: {}", username, year, month);

        if (username == null || year == null || month == null) {
            logger.error("Invalid monthly workload request: missing required fields");
            throw new IllegalArgumentException("Username, year, and month are required");
        }

        Optional<TrainerWorkload> trainerWorkload = repository.findByUsername(username);

        if (trainerWorkload.isEmpty()) {
            logger.warn("No workload found for trainer: {}", username);
            return new MonthlyWorkloadResponse(year, month, 0);
        }

        Integer hours = trainerWorkload.get().getMonthlyWorkload(year, month);
        return new MonthlyWorkloadResponse(year, month, hours);
    }

    private TrainerWorkload createNewTrainerWorkload(WorkloadRequest request) {
        logger.debug("Creating new trainer workload for username: {}", request.getTrainerUsername());
        return new TrainerWorkload(
                request.getTrainerUsername(),
                request.getTrainerFirstName(),
                request.getTrainerLastName(),
                request.getIsActive()
        );
    }

    private void updateTrainerInfo(TrainerWorkload trainerWorkload, WorkloadRequest request) {
        if (!trainerWorkload.getTrainerFirstName().equals(request.getTrainerFirstName()) ||
                !trainerWorkload.getTrainerLastName().equals(request.getTrainerLastName()) ||
                !trainerWorkload.getTrainerStatus().equals(request.getIsActive())) {

            logger.debug("Updating trainer info for username: {}", request.getTrainerUsername());
            trainerWorkload.setTrainerFirstName(request.getTrainerFirstName());
            trainerWorkload.setTrainerLastName(request.getTrainerLastName());
            trainerWorkload.setTrainerStatus(request.getIsActive());
        }
    }
}