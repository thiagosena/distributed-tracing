package com.thiagosena.service.email.application.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thiagosena.service.email.application.web.payloads.EmailResponse;
import com.thiagosena.service.email.domain.exceptions.EmailNotFoundException;
import com.thiagosena.service.email.domain.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EmbeddedKafka(partitions = 1,
        controlledShutdown = true,
        ports = 9992,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9992"})
public class EmailControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmailService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnAllEmailFromDatabase() throws Exception {
        MvcResult result = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/emails"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        List<EmailResponse> response = List.of(objectMapper.readValue(contentAsString, EmailResponse[].class));
        assertEquals(1L, response.get(0).id());
        assertEquals("goku@email.com", response.get(0).email());
        assertEquals("{preloaded-report}", response.get(0).content());
    }

    @Test
    void givenEmail_thenReturnItFromDatabase() throws Exception {
        var email = "goku@email.com";
        MvcResult result = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/emails/" + email))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        EmailResponse response = objectMapper.readValue(contentAsString, EmailResponse.class);
        assertEquals(1L, response.id());
        assertEquals(email, response.email());
        assertEquals("{preloaded-report}", response.content());
    }

    @Test
    void givenEmailId_thenDeleteFromDatabase() throws Exception {
        EmailResponse emailResponse = service.findByEmail("frodo@email.com");
        assertNotNull(emailResponse);

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/emails/" + emailResponse.id()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // check if email was removed from database
        EmailNotFoundException thrown = assertThrows(
                EmailNotFoundException.class,
                () -> service.findByEmail(emailResponse.email()),
                "Expected EmailNotFound, but it didn't"
        );
        assertEquals("Could not find email=" + emailResponse.email(), thrown.getMessage());
    }
}
