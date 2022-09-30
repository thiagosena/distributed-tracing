package com.thiagosena.service.email.resource.listener;

import com.thiagosena.entities.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReportListener {
    private final Logger log = LoggerFactory.getLogger(ReportListener.class);

    @KafkaListener(topics = "reports", groupId = "group")
    public void listenReports(Report report) {
        log.info("Got report with id: {}", report.getId());
        System.out.println("Received Message: " + report);
    }
}
