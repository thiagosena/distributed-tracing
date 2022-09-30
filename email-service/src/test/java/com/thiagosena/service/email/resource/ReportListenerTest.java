package com.thiagosena.service.email.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thiagosena.entities.Report;
import com.thiagosena.service.email.resource.listener.ReportListener;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EmbeddedKafka(partitions = 1,
        controlledShutdown = true,
        ports = 9992,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9992"})
public class ReportListenerTest {

    @Value(value = "${kafka.topic}")
    private String TOPIC_NAME;

    @SpyBean
    private ReportListener reportListener;

    @Captor
    private ArgumentCaptor<Report> reportArgumentCaptor;

    @Autowired
    private ObjectMapper objectMapper;

    private Producer<String, Report> producer;

    @BeforeAll
    void setup(@Autowired EmbeddedKafkaBroker embeddedKafkaBroker) {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        producer = new DefaultKafkaProducerFactory<>(
                configs, new StringSerializer(), new JsonSerializer<Report>(objectMapper)).createProducer();
    }

    @Test
    void listenerReports() {
        // Write a message (John Wick report) to Kafka using a test producer
        var reportId = "1";
        Report report = new Report(Long.parseLong(reportId), 1L, "johnwick-report");
        producer.send(new ProducerRecord<>(TOPIC_NAME, report));

        // Read the message and assert its properties
        verify(this.reportListener, timeout(5000).times(1))
                .listenReports(reportArgumentCaptor.capture());

        Report reportCaptor = reportArgumentCaptor.getValue();
        assertNotNull(reportCaptor);
        assertEquals(reportId, reportCaptor.getId().toString());
        assertEquals(1L, reportCaptor.getUserId());
        assertEquals("johnwick-report", reportCaptor.getReport());
    }
}
