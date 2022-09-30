package com.thiagosena.service.email.domain.exceptions;

public class EmailNotFoundException extends RuntimeException {

    public EmailNotFoundException(String email) {
        super("Could not find email=" + email);
    }
}
