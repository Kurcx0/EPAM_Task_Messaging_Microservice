package com.gymcrm.trainer_workload_service.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MonthSummaryTest {

    @Test
    void constructor_ShouldInitializeDurationToZero() {
        MonthSummary month = new MonthSummary(5);

        assertEquals(5, month.getMonth());
        assertEquals(0, month.getTrainingSummaryDuration());
    }

    @Test
    void addDuration_ShouldIncreaseDuration() {
        MonthSummary month = new MonthSummary(5, 30);

        month.addDuration(60);

        assertEquals(90, month.getTrainingSummaryDuration());
    }

    @Test
    void subtractDuration_ShouldDecreaseDuration() {
        MonthSummary month = new MonthSummary(5, 60);

        month.subtractDuration(30);

        assertEquals(30, month.getTrainingSummaryDuration());
    }

    @Test
    void subtractDuration_ShouldNotGoBelowZero() {
        MonthSummary month = new MonthSummary(5, 30);

        month.subtractDuration(60);

        assertEquals(0, month.getTrainingSummaryDuration());
    }

    @Test
    void equals_ShouldReturnTrue_WhenSameMonth() {
        MonthSummary month1 = new MonthSummary(5);
        MonthSummary month2 = new MonthSummary(5);

        assertEquals(month1, month2);
        assertEquals(month1.hashCode(), month2.hashCode());
    }

    @Test
    void equals_ShouldReturnFalse_WhenDifferentMonth() {
        MonthSummary month1 = new MonthSummary(5);
        MonthSummary month2 = new MonthSummary(6);

        assertNotEquals(month1, month2);
    }
}