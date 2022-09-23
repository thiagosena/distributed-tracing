package com.thiagosena.service.email.domain.service;

import com.thiagosena.entities.Email;
import com.thiagosena.service.email.application.web.payloads.EmailResponse;

import java.util.List;

public interface EmailService {
    List<Email> findAll();

    EmailResponse findByEmail(String email);

    void deleteById(Long id);
}
