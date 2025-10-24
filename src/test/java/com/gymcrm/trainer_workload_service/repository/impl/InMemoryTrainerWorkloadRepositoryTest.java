package com.gymcrm.trainer_workload_service.repository.impl;

import com.gymcrm.trainer_workload_service.model.TrainerWorkload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTrainerWorkloadRepositoryTest {

    private InMemoryTrainerWorkloadRepository repository;
    private TrainerWorkload testWorkload;
    private final String testUsername = "john.doe";

    @BeforeEach
    void setUp() {
        repository = new InMemoryTrainerWorkloadRepository();

        testWorkload = new TrainerWorkload();
        testWorkload.setTrainerUsername(testUsername);
        testWorkload.setTrainerFirstName("John");
        testWorkload.setTrainerLastName("Doe");
        testWorkload.setTrainerStatus(true);
    }

    @Test
    void save_ShouldStoreWorkload() {
        TrainerWorkload savedWorkload = repository.save(testWorkload);

        assertNotNull(savedWorkload);
        assertEquals(testUsername, savedWorkload.getTrainerUsername());
        assertTrue(repository.existsByUsername(testUsername));
    }

    @Test
    void findByUsername_ShouldReturnWorkload_WhenExists() {
        repository.save(testWorkload);

        Optional<TrainerWorkload> result = repository.findByUsername(testUsername);

        assertTrue(result.isPresent());
        assertEquals(testUsername, result.get().getTrainerUsername());
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenNotExists() {
        Optional<TrainerWorkload> result = repository.findByUsername("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllWorkloads() {
        repository.save(testWorkload);

        TrainerWorkload secondWorkload = new TrainerWorkload();
        secondWorkload.setTrainerUsername("jane.smith");
        secondWorkload.setTrainerFirstName("Jane");
        secondWorkload.setTrainerLastName("Smith");
        secondWorkload.setTrainerStatus(true);
        repository.save(secondWorkload);

        List<TrainerWorkload> result = repository.findAll();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(w -> w.getTrainerUsername().equals(testUsername)));
        assertTrue(result.stream().anyMatch(w -> w.getTrainerUsername().equals("jane.smith")));
    }

    @Test
    void deleteByUsername_ShouldRemoveWorkload() {
        repository.save(testWorkload);
        assertTrue(repository.existsByUsername(testUsername));

        repository.deleteByUsername(testUsername);

        assertFalse(repository.existsByUsername(testUsername));
        assertTrue(repository.findByUsername(testUsername).isEmpty());
    }

    @Test
    void existsByUsername_ShouldReturnTrue_WhenExists() {
        repository.save(testWorkload);

        boolean result = repository.existsByUsername(testUsername);

        assertTrue(result);
    }

    @Test
    void existsByUsername_ShouldReturnFalse_WhenNotExists() {
        boolean result = repository.existsByUsername("nonexistent");

        assertFalse(result);
    }

    @Test
    void concurrentAccess_ShouldHandleMultipleThreads() throws InterruptedException {
        repository.save(testWorkload);

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                TrainerWorkload workload = repository.findByUsername(testUsername).orElse(new TrainerWorkload());
                workload.setTrainerUsername(testUsername);
                repository.save(workload);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                TrainerWorkload workload = repository.findByUsername(testUsername).orElse(new TrainerWorkload());
                workload.setTrainerUsername(testUsername);
                repository.save(workload);
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        assertTrue(repository.existsByUsername(testUsername));
        assertNotNull(repository.findByUsername(testUsername).orElse(null));
    }
}