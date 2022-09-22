package com.thiagosena.service.email.controllers;

import com.thiagosena.email.Email;
import com.thiagosena.service.email.exceptions.EmailNotFoundException;
import com.thiagosena.service.email.repositories.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    private final EmailRepository repository;

    public EmailController(EmailRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Email> getAll() {
        log.info("Listing all emails");
        return repository.findAll();
    }

    @GetMapping("/{email}")
    public Email getByEmail(@PathVariable String email) {
        log.info("Listing email with email: {}", email);
        return repository.findByEmail(email).orElseThrow(() -> new EmailNotFoundException(email));
    }

    @DeleteMapping("/{id}")
    public void deleteEmail(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
