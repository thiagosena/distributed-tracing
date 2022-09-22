package com.thiagosena.service.user.controllers;

import com.thiagosena.report.Report;
import com.thiagosena.service.user.exceptions.UserNotFoundException;
import com.thiagosena.service.user.gateway.ReportGateway;
import com.thiagosena.service.user.gateway.payload.ReportDto;
import com.thiagosena.service.user.repositories.UserRepository;
import com.thiagosena.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserRepository repository;

    private final ReportGateway reportGateway;

    public UserController(UserRepository repository, ReportGateway reportGateway) {
        this.repository = repository;
        this.reportGateway = reportGateway;
    }

    @GetMapping
    List<User> getAll() {
        log.info("Listing all users");
        return repository.findAll();
    }

    @PostMapping
    User newUser(@RequestBody User newUser) {
        return repository.save(newUser);
    }

    @GetMapping("/{id}")
    User getOne(@PathVariable Long id) {
        log.info("Listing info about user: {}", id);

        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @ResponseBody
    @PostMapping("/{id}/generate-report")
    Report generateReport(@PathVariable Long id) {
        log.info("Creating new report for user: {}", id);

        ReportDto reportDto = new ReportDto(id, "This new generated report");
        return this.reportGateway.createReportForCustomerId(reportDto);
    }

    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable Long id) {
        repository.deleteById(id);
    }

}
