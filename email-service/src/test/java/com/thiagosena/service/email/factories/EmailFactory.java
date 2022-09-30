package com.thiagosena.service.email.factories;

import com.thiagosena.entities.Email;

import java.util.List;

public class EmailFactory {

    public static List<Email> getEmails() {
        return List.of(
                new Email("email@email.com", "{preloaded-report-1}"),
                new Email("email2@email.com", "{preloaded-report-2}")
        );
    }
}
