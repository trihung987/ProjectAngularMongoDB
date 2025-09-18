package me.trihung.service.impl;

import lombok.RequiredArgsConstructor;
import me.trihung.dto.EventTypeRevenueDto;
import me.trihung.dto.RevenueDataDto;
import me.trihung.dto.TopEventDto;
import me.trihung.repository.OrderRepository;
import me.trihung.service.AnalyticsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) 
public class AnalyticsServiceImpl implements AnalyticsService {

    private final OrderRepository orderRepository;
    private static final int TOP_EVENTS_LIMIT = 5;

    @Override
    public List<RevenueDataDto> getRevenueData(int year) {
        LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusNanos(1);
        return orderRepository.findRevenueDataByDateRange(startDate, endDate);
    }

    @Override
    public List<EventTypeRevenueDto> getEventTypeRevenue(int year, String eventType) {
        LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusNanos(1);

        // Nếu eventType là 'all', truyền null vào repository để bỏ qua điều kiện lọc
        String filterEventType = "all".equalsIgnoreCase(eventType) ? null : eventType;
        
        return orderRepository.findEventTypeRevenue(startDate, endDate, filterEventType);
    }

    @Override
    public List<TopEventDto> getTopEvents(int year) {
        LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime endDate = startDate.plusYears(1).minusNanos(1);

        Pageable topEventsPageable = PageRequest.of(0, TOP_EVENTS_LIMIT);

        return orderRepository.findTopEvents(startDate, endDate, topEventsPageable);
    }
}