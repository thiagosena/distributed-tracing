package com.thiagosena.service.report.controllers;

import com.thiagosena.report.Report;
import com.thiagosena.service.report.controllers.payloads.ReportRequest;
import com.thiagosena.service.report.controllers.payloads.ReportResponse;
import com.thiagosena.service.report.exceptions.ReportNotFoundException;
import com.thiagosena.service.report.repositories.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final ReportRepository repository;
    private final KafkaTemplate<String, Report> kafkaTemplate;

    @Value(value = "${kafka.topic}")
    private String topic;

    public ReportController(ReportRepository repository, KafkaTemplate<String, Report> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportResponse newReport(@RequestBody ReportRequest reportDto) {
        Report newReport = repository.save(new Report(reportDto.userId(), reportDto.report()));
        kafkaTemplate.send(this.topic, newReport);

        return new ReportResponse(newReport.getId(), newReport.getUserId(), newReport.getReport());
    }

    @GetMapping
    List<Report> getAll() {
        log.info("Listing all users");
        return repository.findAll();
    }

    @GetMapping("/{id}")
    Report getById(@PathVariable Long id) {
        log.info("Listing info about user with id: {}", id);
        return repository.findById(id).orElseThrow(() -> new ReportNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    void deleteReport(@PathVariable Long id) {
        log.info("Deleting report with id: {}", id);
        repository.deleteById(id);
    }

}
