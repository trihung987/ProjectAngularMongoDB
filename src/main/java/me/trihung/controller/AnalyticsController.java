package me.trihung.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import me.trihung.dto.EventTypeRevenueDto;
import me.trihung.dto.RevenueDataDto;
import me.trihung.dto.TopEventDto;
import me.trihung.service.AnalyticsService;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/revenue-data")
    public ResponseEntity<List<RevenueDataDto>> getRevenueData(
            @RequestParam(defaultValue = "0") int year) {
        // Nếu client không truyền 'year' hoặc 'year=0', mặc định lấy năm hiện tại
        int queryYear = (year == 0) ? LocalDate.now().getYear() : year;
        List<RevenueDataDto> data = analyticsService.getRevenueData(queryYear);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/event-type-revenue")
    public ResponseEntity<List<EventTypeRevenueDto>> getEventTypeRevenue(
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "all") String eventType) {
        int queryYear = (year == 0) ? LocalDate.now().getYear() : year;
        List<EventTypeRevenueDto> data = analyticsService.getEventTypeRevenue(queryYear, eventType);
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/top-events")
    public ResponseEntity<List<TopEventDto>> getTopEvents(
            @RequestParam(defaultValue = "0") int year) {
        int queryYear = (year == 0) ? LocalDate.now().getYear() : year;
        List<TopEventDto> data = analyticsService.getTopEvents(queryYear);
        return ResponseEntity.ok(data);
    }
}