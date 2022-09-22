package com.thiagosena.service.report.exceptions;

public class ReportNotFoundException extends RuntimeException {

    public ReportNotFoundException(Long id) {
        super("Could not find report " + id);
    }
}