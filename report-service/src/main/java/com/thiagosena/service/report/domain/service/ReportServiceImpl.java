package com.thiagosena.service.report.domain.service;

import com.thiagosena.entities.Report;
import com.thiagosena.service.report.application.web.payloads.ReportRequest;
import com.thiagosena.service.report.application.web.payloads.ReportResponse;
import com.thiagosena.service.report.domain.exceptions.ReportNotFoundException;
import com.thiagosena.service.report.resource.repositories.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ReportRepository repository;
    private final KafkaTemplate<String, Report> kafkaTemplate;

    @Value(value = "${kafka.topic}")
    private String topic;

    public ReportServiceImpl(ReportRepository repository, KafkaTemplate<String, Report> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public ReportResponse newReport(ReportRequest reportRequest) {
        log.info("Saving new report into database");
        final Report newReport = repository.save(new Report(reportRequest.userId(), reportRequest.report()));
        log.info("Send to reports topic");
        kafkaTemplate.send(this.topic, newReport);
        return new ReportResponse(newReport.getId(), newReport.getUserId(), newReport.getReport());
    }

    @Override
    public List<ReportResponse> findAll() {
        log.info("Listing all reports");
        List<Report> reports = repository.findAll();
        return reports
                .stream()
                .map(report -> new ReportResponse(
                        report.getId(),
                        report.getUserId(),
                        report.getReport()
                ))
                .toList();
    }

    @Override
    public ReportResponse findById(Long id) {
        log.info("Listing info about report with id: {}", id);
        Report report = repository.findById(id).orElseThrow(() -> new ReportNotFoundException(id));
        return new ReportResponse(report.getId(), report.getUserId(), report.getReport());
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting report with id: {}", id);
        repository.deleteById(id);
    }
}
