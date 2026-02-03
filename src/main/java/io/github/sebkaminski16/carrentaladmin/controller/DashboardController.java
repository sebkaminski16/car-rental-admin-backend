package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.dto.DashboardDtos;
import io.github.sebkaminski16.carrentaladmin.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardDtos.DashboardSummaryDto> summary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    @GetMapping("/rentals-per-day")
    public ResponseEntity<List<DashboardDtos.CountByDayDto>> rentalsPerDay(@RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(dashboardService.rentalsPerDay(days));
    }
}
