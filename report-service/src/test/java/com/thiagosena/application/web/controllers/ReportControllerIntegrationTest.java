package com.thiagosena.application.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thiagosena.service.report.ReportServiceApplication;
import com.thiagosena.service.report.application.web.payloads.ReportResponse;
import com.thiagosena.service.report.domain.exceptions.ReportNotFoundException;
import com.thiagosena.service.report.domain.service.ReportService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = {ReportServiceApplication.class})
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9099", "port=9099"})
public class ReportControllerIntegrationTest {

    @Value(value = "${kafka.topic}")
    private String TOPIC_NAME;

    private BlockingQueue<ConsumerRecord<String, String>> consumerRecords;

    private KafkaMessageListenerContainer<String, String> container;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReportService service;

    @BeforeAll
    void setUp(@Autowired EmbeddedKafkaBroker embeddedKafkaBroker) {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));
        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), new StringDeserializer());
        ContainerProperties containerProperties = new ContainerProperties(TOPIC_NAME);
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        consumerRecords = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, String>) consumerRecords::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterAll
    void tearDown() {
        container.stop();
    }

    @Test
    void shouldReturnAllReportFromDatabase() throws Exception {
        MvcResult result = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/reports"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        List<ReportResponse> response = List.of(objectMapper.readValue(contentAsString, ReportResponse[].class));
        assertEquals(1L, response.get(0).userId());
        assertEquals("Stole ring and gave it as gift.", response.get(0).report());
        assertEquals(2L, response.get(1).userId());
        assertEquals("Got the ring as the gift", response.get(1).report());
    }

    @Test
    void givenNewReportData_thenSaveOnDatabaseAndSendMessage() throws Exception {
        var report = """
                {
                    "userId": 1,
                    "report": "generating report"
                }
                """;
        var reportSaved = """
                {"id":4,"userId":1,"report":"generating report"}""";

        MvcResult result = this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(report))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        ConsumerRecord<String, String> received = consumerRecords.poll(100, TimeUnit.MILLISECONDS);
        assertNotNull(received);
        assertEquals(received.value(), reportSaved);

        String contentAsString = result.getResponse().getContentAsString();
        ReportResponse response = objectMapper.readValue(contentAsString, ReportResponse.class);
        assertNotNull(response);
        assertEquals(1L, response.userId());
        assertEquals("generating report", response.report());
    }

    @Test
    void givenReportId_thenReturnReportFromDatabase() throws Exception {
        MvcResult result = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/reports/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        ReportResponse response = objectMapper.readValue(contentAsString, ReportResponse.class);
        assertEquals(1L, response.userId());
        assertEquals("Stole ring and gave it as gift.", response.report());
    }

    @Test
    void givenReportId_thenDeleteFromDatabase() throws Exception {
        var reportId = 3L;
        this.mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/reports/" + reportId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // check if report was removed from database
        ReportNotFoundException thrown = assertThrows(
                ReportNotFoundException.class,
                () -> service.findById(reportId),
                "Expected ReportNotFound, but it didn't"
        );
        assertEquals("Could not find report with id=" + reportId, thrown.getMessage());
    }
}
