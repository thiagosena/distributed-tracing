package com.thiagosena.factories;

import com.thiagosena.entities.Report;

import java.util.List;
import java.util.Optional;

public class ReportFactory {
    public static List<Report> getReports() {
        return List.of(
                new Report(1L, 1L, "report 1"),
                new Report(2L, 1L, "report 2"),
                new Report(3L, 1L, "report 3")
        );
    }

    public static Optional<Report> getReport() {
        return Optional.of(new Report(1L, 1L, "new report"));
    }
}
