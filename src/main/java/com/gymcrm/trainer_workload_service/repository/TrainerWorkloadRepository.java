package com.gymcrm.trainer_workload_service.repository;

import com.gymcrm.trainer_workload_service.model.TrainerWorkload;
import java.util.List;
import java.util.Optional;

public interface TrainerWorkloadRepository {
    TrainerWorkload save(TrainerWorkload trainerWorkload);
    Optional<TrainerWorkload> findByUsername(String username);
    List<TrainerWorkload> findAll();
    void deleteByUsername(String username);
    boolean existsByUsername(String username);

    Optional<TrainerWorkload> findById(Long trainerId);
}