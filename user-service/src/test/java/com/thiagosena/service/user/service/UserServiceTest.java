package com.thiagosena.service.user.service;

import com.thiagosena.entities.Report;
import com.thiagosena.entities.User;
import com.thiagosena.service.user.application.web.payloads.UserRequest;
import com.thiagosena.service.user.application.web.payloads.UserResponse;
import com.thiagosena.service.user.domain.exceptions.UserNotFoundException;
import com.thiagosena.service.user.domain.service.UserService;
import com.thiagosena.service.user.domain.service.UserServiceImpl;
import com.thiagosena.service.user.factories.UserFactory;
import com.thiagosena.service.user.resource.gateway.ReportGateway;
import com.thiagosena.service.user.resource.gateway.payload.ReportDto;
import com.thiagosena.service.user.resource.repositories.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private final ReportGateway reportGateway = mock(ReportGateway.class);
    private final UserRepository repository = mock(UserRepository.class);
    private final UserService service = new UserServiceImpl(repository, reportGateway);

    @Test
    void shouldReturnAllUsersOnDatabase() {
        when(repository.findAll()).thenReturn(UserFactory.getUsers());

        List<UserResponse> users = service.getAll();

        verify(repository, times(1)).findAll();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(1, users.get(0).id());
        assertEquals("borabill", users.get(0).username());
    }

    @Test
    void givenUserRequest_thenSaveOnDatabase() {
        var userRequest = new UserRequest("kuston");
        when(repository.save(any())).thenReturn(new User(userRequest.username()));

        UserResponse user = service.save(userRequest);

        verify(repository, times(1)).save(any());

        assertNotNull(user);
        assertEquals(userRequest.username(), user.username());
    }

    @Test
    void givenUserId_thenReturnUser() {
        var userId = 1L;
        when(repository.findById(userId)).thenReturn(Optional.of(UserFactory.getUsers().get(0)));

        UserResponse userResponse = service.findById(userId);

        verify(repository, times(1)).findById(userId);

        assertNotNull(userResponse);
        assertEquals(userId, userResponse.id());
    }

    @Test
    void givenUserId_thenThrowUserNotFoundException() {
        var userId = 0L;
        when(repository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException thrown = assertThrows(
                UserNotFoundException.class,
                () -> service.findById(userId),
                "Expected UserNotFound, but it didn't"
        );

        verify(repository, times(1)).findById(userId);

        assertEquals("Could not find user with id=" + userId, thrown.getMessage());
    }

    @Test
    void givenUserId_thenCreateReportAndSendToReportService() {
        var userId = 1L;
        var reportMessage = "This is a new generated report for user " + userId;
        var reportDto = new ReportDto(userId, reportMessage);
        var reportEntitySaved = new Report(userId, reportDto.userId(), reportMessage);
        when(reportGateway.createReportForCustomer(reportDto)).thenReturn(reportEntitySaved);

        var response = service.createReportForCustomer(userId);
        verify(reportGateway, times(1)).createReportForCustomer(reportDto);

        assertNotNull(response);
        assertEquals(userId, response.id());
        assertEquals(userId, response.userId());
        assertEquals(reportMessage, response.report());

    }

    @Test
    void givenUserId_thenDeleteUser() {
        var userId = 1L;
        doNothing().when(repository).deleteById(userId);
        service.deleteById(userId);

        verify(repository, times(1)).deleteById(userId);
    }
}
