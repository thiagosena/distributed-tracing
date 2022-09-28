package com.thiagosena.service.report.domain.service;

import com.thiagosena.entities.Report;
import com.thiagosena.factories.ReportFactory;
import com.thiagosena.service.report.application.web.payloads.ReportRequest;
import com.thiagosena.service.report.domain.exceptions.ReportNotFoundException;
import com.thiagosena.service.report.resource.repositories.ReportRepository;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReportServiceTest {

    private final KafkaTemplate<String, Report> kafkaTemplate = mock(KafkaTemplate.class);
    private final ReportRepository repository = mock(ReportRepository.class);
    private final ReportService service = new ReportServiceImpl(repository, kafkaTemplate);

    @Test
    void givenNewReportData_thenSaveOnDatabaseAndSendMessage() {
        var reportMessage = "new report";
        var reportDto = new ReportRequest(1L, reportMessage);
        var reportEntitySaved = new Report(1L, reportDto.userId(), reportMessage);

        when(repository.save(any())).thenReturn(reportEntitySaved);

        final var response = service.newReport(reportDto);
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(1L, response.userId());
        assertEquals(reportMessage, response.report());
    }

    @Test
    void findAllReportsOnDatabase() {
        when(repository.findAll()).thenReturn(ReportFactory.getReports());
        List<Report> reports = service.findAll();
        assertEquals(3, reports.size());
    }

    @Test
    void givenReportId_thenReturnReport() {
        var reportId = 1L;
        when(repository.findById(reportId)).thenReturn(ReportFactory.getReport());
        Report report = service.findById(reportId);
        assertNotNull(report);
        assertEquals(reportId, report.getId());
    }

    @Test
    void givenReportId_thenThrowReportNotFoundException() {
        var reportId = 0L;
        when(repository.findById(reportId)).thenReturn(Optional.empty());
        ReportNotFoundException thrown = assertThrows(
                ReportNotFoundException.class,
                () -> service.findById(reportId),
                "Expected ReportNotFound, but it didn't"
        );
        assertEquals("Could not find report with id=" + reportId, thrown.getMessage());
    }

    @Test
    void givenReportId_thenDeleteReport() {
        var reportId = 1L;
        doNothing().when(repository).deleteById(reportId);
        service.deleteById(reportId);

        verify(repository, times(1)).deleteById(reportId);
    }

}
