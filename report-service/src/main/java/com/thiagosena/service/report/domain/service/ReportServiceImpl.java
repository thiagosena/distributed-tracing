package com.thiagosena.service.report.domain.service;

import com.thiagosena.report.Report;
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
    public ReportResponse newReport(ReportRequest reportDto) {
        log.info("Saving new report into database");
        Report newReport = repository.save(new Report(reportDto.userId(), reportDto.report()));
        log.info("Send to reports topic");
        kafkaTemplate.send(this.topic, newReport);
        return new ReportResponse(newReport.getId(), newReport.getUserId(), newReport.getReport());
    }

    @Override
    public List<Report> findAll() {
        log.info("Listing all users");
        return repository.findAll();
    }

    @Override
    public Report findById(Long id) {
        log.info("Listing info about user with id: {}", id);
        return repository.findById(id).orElseThrow(() -> new ReportNotFoundException(id));
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting report with id: {}", id);
        repository.deleteById(id);
    }
}
