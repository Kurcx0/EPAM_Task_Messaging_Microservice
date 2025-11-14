package com.gymcrm.trainer_workload_service.dto.messaging;

import java.io.Serializable;
import java.util.List;

public class TrainerWorkloadResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long trainerId;
    private String username;
    private int totalHours;
    private int totalSessions;
    private List<MonthlyWorkload> monthlyBreakdown;

    public TrainerWorkloadResponse() {
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

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public List<MonthlyWorkload> getMonthlyBreakdown() {
        return monthlyBreakdown;
    }

    public void setMonthlyBreakdown(List<MonthlyWorkload> monthlyBreakdown) {
        this.monthlyBreakdown = monthlyBreakdown;
    }

    @Override
    public String toString() {
        return "TrainerWorkloadResponse{" +
                "trainerId=" + trainerId +
                ", username='" + username + '\'' +
                ", totalHours=" + totalHours +
                ", totalSessions=" + totalSessions +
                ", monthlyBreakdown=" + monthlyBreakdown +
                '}';
    }

    public static class MonthlyWorkload implements Serializable {
        private static final long serialVersionUID = 1L;

        private int year;
        private int month;
        private int hours;
        private int sessions;

        public MonthlyWorkload() {
        }

        public MonthlyWorkload(int year, int month, int hours, int sessions) {
            this.year = year;
            this.month = month;
            this.hours = hours;
            this.sessions = sessions;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getHours() {
            return hours;
        }

        public void setHours(int hours) {
            this.hours = hours;
        }

        public int getSessions() {
            return sessions;
        }

        public void setSessions(int sessions) {
            this.sessions = sessions;
        }

        @Override
        public String toString() {
            return "MonthlyWorkload{" +
                    "year=" + year +
                    ", month=" + month +
                    ", hours=" + hours +
                    ", sessions=" + sessions +
                    '}';
        }
    }
}