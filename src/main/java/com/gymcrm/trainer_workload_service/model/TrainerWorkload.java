package com.gymcrm.trainer_workload_service.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrainerWorkload {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean trainerStatus;
    private List<YearSummary> years;

    public TrainerWorkload() {
        this.years = new ArrayList<>();
    }

    public TrainerWorkload(String trainerUsername, String trainerFirstName, String trainerLastName, Boolean trainerStatus) {
        this.trainerUsername = trainerUsername;
        this.trainerFirstName = trainerFirstName;
        this.trainerLastName = trainerLastName;
        this.trainerStatus = trainerStatus;
        this.years = new ArrayList<>();
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    public String getTrainerFirstName() {
        return trainerFirstName;
    }

    public void setTrainerFirstName(String trainerFirstName) {
        this.trainerFirstName = trainerFirstName;
    }

    public String getTrainerLastName() {
        return trainerLastName;
    }

    public void setTrainerLastName(String trainerLastName) {
        this.trainerLastName = trainerLastName;
    }

    public Boolean getTrainerStatus() {
        return trainerStatus;
    }

    public void setTrainerStatus(Boolean trainerStatus) {
        this.trainerStatus = trainerStatus;
    }

    public List<YearSummary> getYears() {
        return years;
    }

    public void setYears(List<YearSummary> years) {
        this.years = years;
    }

    public Long getId() {
        if (trainerUsername != null && trainerUsername.startsWith("trainer")) {
            try {
                return Long.parseLong(trainerUsername.substring(7));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public String getUsername() {
        return getTrainerUsername();
    }

    public List<YearSummary> getYearSummaries() {
        return getYears();
    }

    public YearSummary getOrCreateYear(Integer year) {
        Optional<YearSummary> existingYear = years.stream()
                .filter(y -> y.getYear().equals(year))
                .findFirst();

        if (existingYear.isPresent()) {
            return existingYear.get();
        } else {
            YearSummary newYear = new YearSummary(year);
            years.add(newYear);
            return newYear;
        }
    }

    public Integer getMonthlyWorkload(Integer year, Integer month) {
        Optional<YearSummary> yearSummary = years.stream()
                .filter(y -> y.getYear().equals(year))
                .findFirst();

        if (yearSummary.isEmpty()) {
            return 0;
        }

        Optional<MonthSummary> monthSummary = yearSummary.get().getMonths().stream()
                .filter(m -> m.getMonth().equals(month))
                .findFirst();

        return monthSummary.map(MonthSummary::getTrainingSummaryDuration).orElse(0);
    }
}