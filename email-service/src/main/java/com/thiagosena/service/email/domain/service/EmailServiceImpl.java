package com.thiagosena.service.email.domain.service;

import com.thiagosena.service.email.application.web.payloads.EmailResponse;
import com.thiagosena.service.email.domain.exceptions.EmailNotFoundException;
import com.thiagosena.service.email.resource.repositories.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final EmailRepository repository;

    public EmailServiceImpl(EmailRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<EmailResponse> findAll() {
        log.info("Listing all emails");
        return repository.findAll().stream().map(EmailResponse::new).toList();
    }

    @Override
    public EmailResponse findByEmail(String email) {
        log.info("Listing email with email: {}", email);
        return new EmailResponse(repository.findByEmail(email).orElseThrow(
                () -> new EmailNotFoundException(email))
        );
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting email with id: {}", id);
        repository.deleteById(id);
    }
}
