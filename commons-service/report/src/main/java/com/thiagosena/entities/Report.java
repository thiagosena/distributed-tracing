package com.thiagosena.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Report {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private String report;

    public Report() {
    }

    public Report(Long userId, String report) {
        this.userId = userId;
        this.report = report;
    }

    public Report(Long id, Long userId, String report) {
        this.id = id;
        this.userId = userId;
        this.report = report;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getReport() {
        return report;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", userId=" + userId +
                ", report='" + report + '\'' +
                '}';
    }
}
