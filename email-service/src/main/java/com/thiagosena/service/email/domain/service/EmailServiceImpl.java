package com.thiagosena.service.email.domain.service;

import com.thiagosena.entities.Email;
import com.thiagosena.entities.Report;
import com.thiagosena.service.email.application.web.payloads.EmailResponse;
import com.thiagosena.service.email.domain.exceptions.EmailNotFoundException;
import com.thiagosena.service.email.resource.repositories.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final EmailRepository repository;

    public EmailServiceImpl(EmailRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "reports", groupId = "group")
    public void listenReports(Report report) {
        log.info("Got report with id: {}", report.getId());
        System.out.println("Received Message: " + report);
    }

    @Override
    public List<Email> findAll() {
        log.info("Listing all emails");
        return repository.findAll();
    }

    @Override
    public EmailResponse findByEmail(String email) {
        log.info("Listing email with email: {}", email);
        Email emailEntity = repository.findByEmail(email).orElseThrow(() -> new EmailNotFoundException(email));
        return new EmailResponse(emailEntity.getId(), emailEntity.getEmail(), emailEntity.getContent());
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting email with id: {}", id);
        repository.deleteById(id);
    }
}
