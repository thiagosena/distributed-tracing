package com.thiagosena.service.user.domain.service;

import com.thiagosena.service.user.application.web.payloads.ReportResponse;
import com.thiagosena.service.user.application.web.payloads.UserRequest;
import com.thiagosena.service.user.application.web.payloads.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAll();

    UserResponse save(UserRequest newUser);

    UserResponse findById(Long id);

    ReportResponse createReportForCustomerId(Long id);

    void deleteById(Long id);
}
