package com.thiagosena.service.email.application.web.payloads;

import com.thiagosena.entities.Email;

public record EmailResponse(Long id, String email, String content) {
    public EmailResponse(Email email) {
        this(email.getId(), email.getEmail(), email.getContent());
    }
}
