package com.gymcrm.trainer_workload_service.model;

import java.util.Objects;

public class MonthSummary {
    private Integer month;
    private Integer trainingSummaryDuration;

    public MonthSummary() {
        this.trainingSummaryDuration = 0;
    }

    public MonthSummary(Integer month) {
        this.month = month;
        this.trainingSummaryDuration = 0;
    }

    public MonthSummary(Integer month, Integer trainingSummaryDuration) {
        this.month = month;
        this.trainingSummaryDuration = trainingSummaryDuration;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getTrainingSummaryDuration() {
        return trainingSummaryDuration;
    }

    public void setTrainingSummaryDuration(Integer trainingSummaryDuration) {
        this.trainingSummaryDuration = trainingSummaryDuration;
    }

    public void addDuration(Integer duration) {
        this.trainingSummaryDuration += duration;
    }

    public void subtractDuration(Integer duration) {
        this.trainingSummaryDuration -= duration;
        if (this.trainingSummaryDuration < 0) {
            this.trainingSummaryDuration = 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonthSummary that = (MonthSummary) o;
        return Objects.equals(month, that.month);
    }

    @Override
    public int hashCode() {
        return Objects.hash(month);
    }
}