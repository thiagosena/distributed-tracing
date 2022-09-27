package com.thiagosena.service.report.domain.exceptions;

public class ReportNotFoundException extends RuntimeException {

    public ReportNotFoundException(Long id) {
        super("Could not find report with id=" + id);
    }
}