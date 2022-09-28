package com.thiagosena.service.report.domain.service;

import com.thiagosena.service.report.application.web.payloads.ReportRequest;
import com.thiagosena.service.report.application.web.payloads.ReportResponse;

import java.util.List;

public interface ReportService {
    ReportResponse newReport(ReportRequest reportDto);

    List<ReportResponse> findAll();

    ReportResponse findById(Long id);

    void deleteById(Long id);
}
