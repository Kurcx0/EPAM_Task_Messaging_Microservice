package com.gymcrm.trainer_workload_service.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class YearSummaryTest {

    @Test
    void getOrCreateMonth_ShouldCreateNewMonth_WhenNotExists() {
        YearSummary year = new YearSummary(2024);
        year.setMonths(new ArrayList<>());

        MonthSummary month = year.getOrCreateMonth(5);

        assertNotNull(month);
        assertEquals(5, month.getMonth());
        assertEquals(1, year.getMonths().size());
    }

    @Test
    void getOrCreateMonth_ShouldReturnExistingMonth_WhenExists() {
        YearSummary year = new YearSummary(2024);
        year.setMonths(new ArrayList<>());

        MonthSummary existingMonth = new MonthSummary(5, 0);
        year.getMonths().add(existingMonth);

        MonthSummary result = year.getOrCreateMonth(5);

        assertSame(existingMonth, result);
        assertEquals(1, year.getMonths().size());
    }

    @Test
    void equals_ShouldReturnTrue_WhenSameYear() {
        YearSummary year1 = new YearSummary(2024);
        YearSummary year2 = new YearSummary(2024);

        assertEquals(year1, year2);
        assertEquals(year1.hashCode(), year2.hashCode());
    }

    @Test
    void equals_ShouldReturnFalse_WhenDifferentYear() {
        YearSummary year1 = new YearSummary(2024);
        YearSummary year2 = new YearSummary(2025);

        assertNotEquals(year1, year2);
    }
}