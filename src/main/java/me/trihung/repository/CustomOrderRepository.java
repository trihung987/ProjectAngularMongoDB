package me.trihung.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import me.trihung.dto.EventTypeRevenueDto;
import me.trihung.dto.OrderDto;
import me.trihung.dto.RevenueDataDto;
import me.trihung.dto.TopEventDto;
import me.trihung.entity.User;

public interface CustomOrderRepository {
    Page<OrderDto> findOrderDtosByOwner(User owner, Pageable pageable);
    List<RevenueDataDto> findRevenueDataByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<EventTypeRevenueDto> findEventTypeRevenue(LocalDateTime startDate, LocalDateTime endDate, String eventType);
    List<TopEventDto> findTopEvents(LocalDateTime startDate, LocalDateTime endDate, String eventType, Pageable pageable);
}