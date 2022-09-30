package com.thiagosena.service.email.service;

import com.thiagosena.entities.Email;
import com.thiagosena.service.email.application.web.payloads.EmailResponse;
import com.thiagosena.service.email.domain.exceptions.EmailNotFoundException;
import com.thiagosena.service.email.domain.service.EmailService;
import com.thiagosena.service.email.domain.service.EmailServiceImpl;
import com.thiagosena.service.email.factories.EmailFactory;
import com.thiagosena.service.email.resource.repositories.EmailRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

    private final EmailRepository repository = mock(EmailRepository.class);

    private final EmailService service = new EmailServiceImpl(repository);

    @Test
    void shouldReturnAllEmailsOnDatabase() {
        when(repository.findAll()).thenReturn(EmailFactory.getEmails());

        List<EmailResponse> emails = service.findAll();

        verify(repository, times(1)).findAll();
        assertNotNull(emails);
        assertEquals("email@email.com", emails.get(0).email());
        assertEquals("{preloaded-report-1}", emails.get(0).content());
    }

    @Test
    void givenEmail_thenReturnIdFromDatabase() {
        var email = "email@email.com";
        when(repository.findByEmail(email)).thenReturn(Optional.of(
                new Email(email, "content")
        ));

        EmailResponse emailResponse = service.findByEmail(email);

        verify(repository, times(1)).findByEmail(email);
        assertNotNull(email);
        assertEquals("email@email.com", emailResponse.email());
        assertEquals("content", emailResponse.content());
    }

    @Test
    void givenEmail_thenThrowEmailNotFoundException() {
        var email = "notfound@email.com";
        when(repository.findByEmail(email)).thenReturn(Optional.empty());
        EmailNotFoundException thrown = assertThrows(
                EmailNotFoundException.class,
                () -> service.findByEmail(email),
                "Expected ReportNotFound, but it didn't"
        );
        verify(repository, times(1)).findByEmail(email);
        assertEquals("Could not find email=" + email, thrown.getMessage());
    }

    @Test
    void givenEmailId_thenDeleteIt() {
        var emailId = 1L;
        doNothing().when(repository).deleteById(emailId);

        service.deleteById(emailId);

        verify(repository, times(1)).deleteById(emailId);
    }

}
