package com.gymcrm.trainer_workload_service.dto.messaging;

import java.io.Serializable;
import java.time.LocalDate;

public class TrainerWorkloadRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long trainerId;
    private String username;
    private LocalDate fromDate;
    private LocalDate toDate;

    public TrainerWorkloadRequest() {
    }

    public TrainerWorkloadRequest(Long trainerId, String username, LocalDate fromDate, LocalDate toDate) {
        this.trainerId = trainerId;
        this.username = username;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return "TrainerWorkloadRequest{" +
                "trainerId=" + trainerId +
                ", username='" + username + '\'' +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                '}';
    }
}