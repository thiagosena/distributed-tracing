package com.thiagosena.service.report.application.web.controllers;

import com.thiagosena.service.report.application.web.payloads.ReportRequest;
import com.thiagosena.service.report.application.web.payloads.ReportResponse;
import com.thiagosena.service.report.domain.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportResponse newReport(@RequestBody ReportRequest reportDto) {
        return service.newReport(reportDto);
    }

    @GetMapping
    List<ReportResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    ReportResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteReport(@PathVariable Long id) {
        service.deleteById(id);
    }

}
