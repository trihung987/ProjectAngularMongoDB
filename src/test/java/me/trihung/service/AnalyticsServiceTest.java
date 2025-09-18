package me.trihung.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.trihung.dto.EventTypeRevenueDto;
import me.trihung.dto.RevenueDataDto;
import me.trihung.dto.TopEventDto;
import me.trihung.repository.OrderRepository;
import me.trihung.service.impl.AnalyticsServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AnalyticsServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    private RevenueDataDto sampleRevenueData;
    private EventTypeRevenueDto sampleEventTypeRevenue;
    private TopEventDto sampleTopEvent;

    @BeforeEach
    void setUp() {
        sampleRevenueData = RevenueDataDto.builder()
                .month("2024-01")
                .revenue(new BigDecimal("1000.00"))
                .orders(10L)
                .events(3L)
                .build();

        sampleEventTypeRevenue = new EventTypeRevenueDto(
                "Concert", 50L, new BigDecimal("5000.00"));

        sampleTopEvent = new TopEventDto(
                "Rock Concert", new BigDecimal("2000.00"), 100L, "upcoming");
    }

    @Test
    void testGetRevenueData() {
        // Given
        List<RevenueDataDto> expectedData = Arrays.asList(sampleRevenueData);
        when(orderRepository.findRevenueDataByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedData);

        // When
        List<RevenueDataDto> result = analyticsService.getRevenueData(2024);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleRevenueData.getMonth(), result.get(0).getMonth());
        assertEquals(sampleRevenueData.getRevenue(), result.get(0).getRevenue());
        assertEquals(sampleRevenueData.getOrders(), result.get(0).getOrders());
        assertEquals(sampleRevenueData.getEvents(), result.get(0).getEvents());

        verify(orderRepository).findRevenueDataByDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testGetEventTypeRevenueWithSpecificType() {
        // Given
        List<EventTypeRevenueDto> expectedData = Arrays.asList(sampleEventTypeRevenue);
        when(orderRepository.findEventTypeRevenue(any(LocalDateTime.class), any(LocalDateTime.class), eq("Concert")))
                .thenReturn(expectedData);

        // When
        List<EventTypeRevenueDto> result = analyticsService.getEventTypeRevenue(2024, "Concert");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Concert", result.get(0).getName());
        assertEquals(50L, result.get(0).getValue());
        assertEquals(new BigDecimal("5000.00"), result.get(0).getRevenue());

        verify(orderRepository).findEventTypeRevenue(any(LocalDateTime.class), any(LocalDateTime.class), eq("Concert"));
    }

    @Test
    void testGetEventTypeRevenueWithAllTypes() {
        // Given
        List<EventTypeRevenueDto> expectedData = Arrays.asList(sampleEventTypeRevenue);
        when(orderRepository.findEventTypeRevenue(any(LocalDateTime.class), any(LocalDateTime.class), isNull()))
                .thenReturn(expectedData);

        // When
        List<EventTypeRevenueDto> result = analyticsService.getEventTypeRevenue(2024, "all");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        // Verify that null was passed for eventType (meaning all types)
        verify(orderRepository).findEventTypeRevenue(any(LocalDateTime.class), any(LocalDateTime.class), isNull());
    }

    @Test
    void testGetTopEvents() {
        // Given
        List<TopEventDto> expectedData = Arrays.asList(sampleTopEvent);
        when(orderRepository.findTopEvents(any(LocalDateTime.class), any(LocalDateTime.class), any()))
                .thenReturn(expectedData);

        // When
        List<TopEventDto> result = analyticsService.getTopEvents(2024);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Rock Concert", result.get(0).getName());
        assertEquals(new BigDecimal("2000.00"), result.get(0).getRevenue());
        assertEquals(100L, result.get(0).getTickets());
        assertEquals("upcoming", result.get(0).getStatus());

        verify(orderRepository).findTopEvents(any(LocalDateTime.class), any(LocalDateTime.class), any());
    }
}