package com.thiagosena.service.email.domain.service;

import com.thiagosena.service.email.application.web.payloads.EmailResponse;

import java.util.List;

public interface EmailService {
    List<EmailResponse> findAll();

    EmailResponse findByEmail(String email);

    void deleteById(Long id);
}
