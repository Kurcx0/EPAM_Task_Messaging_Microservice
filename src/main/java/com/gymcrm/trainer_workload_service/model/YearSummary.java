package com.gymcrm.trainer_workload_service.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class YearSummary {
    private Integer year;
    private List<MonthSummary> months;

    public YearSummary() {
        this.months = new ArrayList<>();
    }

    public YearSummary(Integer year) {
        this.year = year;
        this.months = new ArrayList<>();
    }

    public YearSummary(Integer year, List<MonthSummary> months) {
        this.year = year;
        this.months = months;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<MonthSummary> getMonths() {
        return months;
    }

    public void setMonths(List<MonthSummary> months) {
        this.months = months;
    }

    public MonthSummary getOrCreateMonth(Integer month) {
        Optional<MonthSummary> existingMonth = months.stream()
                .filter(m -> m.getMonth().equals(month))
                .findFirst();

        if (existingMonth.isPresent()) {
            return existingMonth.get();
        } else {
            MonthSummary newMonth = new MonthSummary(month);
            months.add(newMonth);
            return newMonth;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YearSummary that = (YearSummary) o;
        return Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year);
    }
}