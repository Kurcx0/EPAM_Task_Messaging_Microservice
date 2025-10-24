package com.gymcrm.trainer_workload_service.controller;

import com.gymcrm.trainer_workload_service.dto.MonthlyWorkloadResponse;
import com.gymcrm.trainer_workload_service.dto.WorkloadRequest;
import com.gymcrm.trainer_workload_service.model.TrainerWorkload;
import com.gymcrm.trainer_workload_service.service.TrainerWorkloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workload")
@Tag(name = "Trainer Workload", description = "Trainer workload operations")
public class WorkloadController {

    private static final Logger logger = LoggerFactory.getLogger(WorkloadController.class);

    @Autowired
    private TrainerWorkloadService workloadService;

    @PostMapping
    @Operation(
            summary = "Process trainer workload",
            description = "Process trainer workload data (ADD or DELETE operation)"
    )
    @ApiResponse(responseCode = "200", description = "Workload processed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    public ResponseEntity<Void> processWorkload(@RequestBody WorkloadRequest request) {
        String transactionId = MDC.get("transactionId");
        logger.info("Transaction [{}] - Received workload request for trainer: {}", transactionId, request.getTrainerUsername());

        try {
            workloadService.processWorkload(request);
            logger.info("Transaction [{}] - Successfully processed workload for trainer: {}", transactionId, request.getTrainerUsername());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Transaction [{}] - Error processing workload request: {}", transactionId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Transaction [{}] - Unexpected error processing workload request: {}", transactionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{username}")
    @Operation(
            summary = "Get trainer workload",
            description = "Get trainer's workload summary"
    )
    @ApiResponse(responseCode = "200", description = "Retrieved workload successfully")
    @ApiResponse(responseCode = "404", description = "Trainer workload not found")
    public ResponseEntity<TrainerWorkload> getTrainerWorkload(
            @Parameter(description = "Trainer's username") @PathVariable String username) {
        String transactionId = MDC.get("transactionId");
        logger.info("Transaction [{}] - Retrieving workload for trainer: {}", transactionId, username);

        try {
            TrainerWorkload workload = workloadService.getTrainerWorkload(username);
            logger.info("Transaction [{}] - Successfully retrieved workload for trainer: {}", transactionId, username);
            return ResponseEntity.ok(workload);
        } catch (IllegalArgumentException e) {
            logger.warn("Transaction [{}] - Trainer workload not found: {}", transactionId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Transaction [{}] - Error retrieving workload: {}", transactionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{username}/{year}/{month}")
    @Operation(
            summary = "Get monthly workload",
            description = "Get trainer's monthly workload"
    )
    @ApiResponse(responseCode = "200", description = "Retrieved monthly workload successfully")
    public ResponseEntity<MonthlyWorkloadResponse> getMonthlyWorkload(
            @Parameter(description = "Trainer's username") @PathVariable String username,
            @Parameter(description = "Year") @PathVariable Integer year,
            @Parameter(description = "Month (1-12)") @PathVariable Integer month) {
        String transactionId = MDC.get("transactionId");
        logger.info("Transaction [{}] - Retrieving monthly workload for trainer: {}, year: {}, month: {}",
                transactionId, username, year, month);

        try {
            MonthlyWorkloadResponse response = workloadService.getMonthlyWorkload(username, year, month);
            logger.info("Transaction [{}] - Successfully retrieved monthly workload", transactionId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Transaction [{}] - Error retrieving monthly workload: {}", transactionId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Transaction [{}] - Unexpected error retrieving monthly workload: {}", transactionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @Operation(
            summary = "Get all trainer workloads",
            description = "Get all trainers' workload summaries"
    )
    @ApiResponse(responseCode = "200", description = "Retrieved all workloads successfully")
    public ResponseEntity<List<TrainerWorkload>> getAllWorkloads() {
        String transactionId = MDC.get("transactionId");
        logger.info("Transaction [{}] - Retrieving all trainer workloads", transactionId);

        try {
            List<TrainerWorkload> workloads = workloadService.getAllTrainerWorkloads();
            logger.info("Transaction [{}] - Successfully retrieved {} workloads", transactionId, workloads.size());
            return ResponseEntity.ok(workloads);
        } catch (Exception e) {
            logger.error("Transaction [{}] - Error retrieving all workloads: {}", transactionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}