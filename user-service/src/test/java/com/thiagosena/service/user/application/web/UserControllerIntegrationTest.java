package com.thiagosena.service.user.application.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.thiagosena.entities.Report;
import com.thiagosena.service.user.UserServiceApplication;
import com.thiagosena.service.user.application.web.payloads.ReportResponse;
import com.thiagosena.service.user.application.web.payloads.UserResponse;
import com.thiagosena.service.user.domain.exceptions.UserNotFoundException;
import com.thiagosena.service.user.domain.service.UserService;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = UserServiceApplication.class)
public class UserControllerIntegrationTest {

    private static final WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(5903)); //No-args constructor will start on port 8080, no HTTPS
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserService service;

    @Test
    void shouldReturnAllUsersFromDatabase() throws Exception {
        MvcResult result = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();

        List<UserResponse> response = List.of(mapper.readValue(contentAsString, UserResponse[].class));
        assertNotNull(response);
        assertEquals(1L, response.get(0).id());
        assertEquals("joquebede", response.get(0).username());

    }

    @Test
    void givenNewUserData_thanSaveOnDatabase() throws Exception {
        var userJson = """
                {
                    "username": "maryjane"
                }
                """;

        var result = this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        var contentAsString = result.getResponse().getContentAsString();
        var response = mapper.readValue(contentAsString, UserResponse.class);

        assertNotNull(response);
        assertEquals(3L, response.id());
        assertEquals("maryjane", response.username());

    }

    @Test
    void givenUserId_thenReturnUserFromDatabase() throws Exception {
        var userId = 1L;
        MvcResult result = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/users/" + userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        UserResponse response = mapper.readValue(contentAsString, UserResponse.class);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("joquebede", response.username());
    }

    @Test
    void givenUserId_thenCreateReport() throws Exception {
        var userId = 1L;
        var reportText = "This is a new generated report for user " + userId;
        String reportJson = mapper.writeValueAsString(new Report(3L, userId, reportText));

        wireMockServer.start();

        wireMockServer.stubFor(
                post("/api/reports").willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.CREATED_201)
                        .withBody(reportJson)));

        MvcResult result = this.mockMvc
                .perform(MockMvcRequestBuilders.post("/api/users/" + userId + "/generate-report"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        ReportResponse response = mapper.readValue(contentAsString, ReportResponse.class);
        assertEquals(3L, response.id());
        assertEquals(userId, response.userId());
        assertEquals(reportText, response.report());

        wireMockServer.stop();

    }

    @Test
    void givenUserId_thenDeleteFromDatabase() throws Exception {
        var userId = 2L;
        this.mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/users/" + userId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        UserNotFoundException thrown = assertThrows(
                UserNotFoundException.class,
                () -> service.findById(userId),
                "Expected ReportNotFound, but it didn't"
        );
        assertEquals("Could not find user with id=" + userId, thrown.getMessage());
    }
}
