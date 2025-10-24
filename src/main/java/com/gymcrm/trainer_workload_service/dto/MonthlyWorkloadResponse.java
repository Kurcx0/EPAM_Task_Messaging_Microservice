package com.gymcrm.trainer_workload_service.dto;

public class MonthlyWorkloadResponse {
    private Integer year;
    private Integer month;
    private Integer hours;

    public MonthlyWorkloadResponse() {
    }

    public MonthlyWorkloadResponse(Integer year, Integer month, Integer hours) {
        this.year = year;
        this.month = month;
        this.hours = hours;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }
}