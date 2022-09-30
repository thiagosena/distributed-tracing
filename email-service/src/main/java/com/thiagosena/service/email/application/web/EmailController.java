package com.thiagosena.service.email.application.web;

import com.thiagosena.service.email.application.web.payloads.EmailResponse;
import com.thiagosena.service.email.domain.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private final EmailService service;

    public EmailController(EmailService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmailResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{email}")
    public EmailResponse getByEmail(@PathVariable String email) {
        return service.findByEmail(email);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmail(@PathVariable Long id) {
        service.deleteById(id);
    }
}
