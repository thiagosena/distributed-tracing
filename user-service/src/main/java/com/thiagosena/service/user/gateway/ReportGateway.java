package com.thiagosena.service.user.gateway;

import com.thiagosena.report.Report;
import com.thiagosena.service.user.gateway.payload.ReportDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(value = "feignReports", url = "${services.report.url}")
public interface ReportGateway {

    @PostMapping("/api/reports")
    @ResponseStatus(HttpStatus.CREATED)
    Report createReportForCustomerId(ReportDto reportDto);
}
