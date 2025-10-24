package com.gymcrm.trainer_workload_service.repository.impl;

import com.gymcrm.trainer_workload_service.model.TrainerWorkload;
import com.gymcrm.trainer_workload_service.repository.TrainerWorkloadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTrainerWorkloadRepository implements TrainerWorkloadRepository {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryTrainerWorkloadRepository.class);
    private final ConcurrentHashMap<String, TrainerWorkload> storage = new ConcurrentHashMap<>();

    @Override
    public TrainerWorkload save(TrainerWorkload trainerWorkload) {
        logger.debug("Saving trainer workload for username: {}", trainerWorkload.getTrainerUsername());
        storage.put(trainerWorkload.getTrainerUsername(), trainerWorkload);
        return trainerWorkload;
    }

    @Override
    public Optional<TrainerWorkload> findByUsername(String username) {
        logger.debug("Finding trainer workload for username: {}", username);
        return Optional.ofNullable(storage.get(username));
    }

    @Override
    public List<TrainerWorkload> findAll() {
        logger.debug("Finding all trainer workloads");
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteByUsername(String username) {
        logger.debug("Deleting trainer workload for username: {}", username);
        storage.remove(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        boolean exists = storage.containsKey(username);
        logger.debug("Checking if trainer workload exists for username: {}, result: {}", username, exists);
        return exists;
    }
}