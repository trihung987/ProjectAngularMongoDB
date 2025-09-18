package me.trihung.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import me.trihung.dto.EventTypeRevenueDto;
import me.trihung.dto.OrderDto;
import me.trihung.dto.OrderPageResponse;
import me.trihung.dto.RevenueDataDto;
import me.trihung.dto.TopEventDto;
import me.trihung.entity.Reservation;

public interface OrderService {
    OrderDto createOrderFromReservation(Reservation reservation);
    OrderPageResponse getOrdersPaged(
            int page,
            int size,
            String sortBy,
            String sortDirection
    );
    OrderDto getOrderById(UUID id);
    
    // Analytics methods that return the same DTOs as before
    List<RevenueDataDto> getRevenueDataByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<EventTypeRevenueDto> getEventTypeRevenue(LocalDateTime startDate, LocalDateTime endDate, String eventType);
    List<TopEventDto> getTopEvents(LocalDateTime startDate, LocalDateTime endDate, int limit);
}
