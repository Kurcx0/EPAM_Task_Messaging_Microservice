package com.gymcrm.trainer_workload_service.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TrainerWorkloadTest {

    @Test
    void getOrCreateYear_ShouldCreateNewYear_WhenNotExists() {
        TrainerWorkload workload = new TrainerWorkload();
        workload.setYears(new ArrayList<>());

        YearSummary year = workload.getOrCreateYear(2024);

        assertNotNull(year);
        assertEquals(2024, year.getYear());
        assertEquals(1, workload.getYears().size());
    }

    @Test
    void getOrCreateYear_ShouldReturnExistingYear_WhenExists() {
        TrainerWorkload workload = new TrainerWorkload();
        workload.setYears(new ArrayList<>());

        YearSummary existingYear = new YearSummary(2024);
        workload.getYears().add(existingYear);

        YearSummary result = workload.getOrCreateYear(2024);

        assertSame(existingYear, result);
        assertEquals(1, workload.getYears().size());
    }

    @Test
    void getMonthlyWorkload_ShouldReturnZero_WhenYearNotExists() {
        TrainerWorkload workload = new TrainerWorkload();
        workload.setYears(new ArrayList<>());

        Integer result = workload.getMonthlyWorkload(2024, 5);

        assertEquals(0, result);
    }

    @Test
    void getMonthlyWorkload_ShouldReturnZero_WhenMonthNotExists() {
        TrainerWorkload workload = new TrainerWorkload();
        workload.setYears(new ArrayList<>());
        YearSummary year = new YearSummary(2024);
        workload.getYears().add(year);

        Integer result = workload.getMonthlyWorkload(2024, 5);

        assertEquals(0, result);
    }

    @Test
    void getMonthlyWorkload_ShouldReturnDuration_WhenExists() {
        TrainerWorkload workload = new TrainerWorkload();
        workload.setYears(new ArrayList<>());

        YearSummary year = new YearSummary(2024);
        workload.getYears().add(year);

        MonthSummary month = new MonthSummary(5, 60);
        year.getMonths().add(month);

        Integer result = workload.getMonthlyWorkload(2024, 5);

        assertEquals(60, result);
    }
}