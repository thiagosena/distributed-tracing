package com.thiagosena.service.user.application.web;

import com.thiagosena.service.user.application.web.payloads.ReportResponse;
import com.thiagosena.service.user.application.web.payloads.UserRequest;
import com.thiagosena.service.user.application.web.payloads.UserResponse;
import com.thiagosena.service.user.domain.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    List<UserResponse> getAll() {
        return service.getAll();
    }

    @PostMapping
    UserResponse newUser(@RequestBody UserRequest newUser) {
        return service.save(newUser);
    }

    @GetMapping("/{id}")
    UserResponse getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    @ResponseBody
    @PostMapping("/{id}/generate-report")
    ReportResponse generateReport(@PathVariable Long id) {
        return this.service.createReportForCustomer(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable Long id) {
        service.deleteById(id);
    }

}
