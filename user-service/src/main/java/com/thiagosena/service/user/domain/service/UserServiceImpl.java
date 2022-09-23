package com.thiagosena.service.user.domain.service;

import com.thiagosena.report.Report;
import com.thiagosena.service.user.application.web.payloads.ReportResponse;
import com.thiagosena.service.user.application.web.payloads.UserRequest;
import com.thiagosena.service.user.application.web.payloads.UserResponse;
import com.thiagosena.service.user.domain.exceptions.UserNotFoundException;
import com.thiagosena.service.user.resource.gateway.ReportGateway;
import com.thiagosena.service.user.resource.gateway.payload.ReportDto;
import com.thiagosena.service.user.resource.repositories.UserRepository;
import com.thiagosena.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository repository;

    private final ReportGateway reportGateway;

    public UserServiceImpl(UserRepository repository, ReportGateway reportGateway) {
        this.repository = repository;
        this.reportGateway = reportGateway;
    }

    @Override
    public List<UserResponse> getAll() {
        log.info("Listing all users");
        List<User> users = repository.findAll();
        return users.stream().map(user -> new UserResponse(user.getId(), user.getUsername())).toList();
    }

    @Override
    public UserResponse save(UserRequest newUser) {
        log.info("Saving user with username: {}", newUser.username());
        User user = repository.save(new User(newUser.username()));
        return new UserResponse(user.getId(), user.getUsername());
    }

    @Override
    public UserResponse findById(Long id) {
        log.info("Listing info about user with id: {}", id);
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        return new UserResponse(user.getId(), user.getUsername());
    }

    @Override
    public ReportResponse createReportForCustomerId(Long id) {
        log.info("Creating new report for user: {}", id);

        ReportDto reportDto = new ReportDto(id, "This is a new generated report for user " + id);
        Report reportEntity = this.reportGateway.createReportForCustomerId(reportDto);

        return new ReportResponse(reportEntity.getId(), reportEntity.getUserId(), reportEntity.getReport());
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
