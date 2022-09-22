package com.thiagosena.service.email.exceptions;

public class EmailNotFoundException extends RuntimeException {

    public EmailNotFoundException(String email) {
        super("Could not find report with email: " + email);
    }
}
