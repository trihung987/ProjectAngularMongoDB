package me.trihung.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.trihung.dto.EventTypeRevenueDto;
import me.trihung.dto.OrderDto;
import me.trihung.dto.OrderPageResponse;
import me.trihung.dto.RevenueDataDto;
import me.trihung.dto.TopEventDto;
import me.trihung.entity.Order;
import me.trihung.entity.Reservation;
import me.trihung.entity.User;
import me.trihung.mapper.OrderMapper;
import me.trihung.repository.OrderRepository;
import me.trihung.service.OrderService;
import me.trihung.helper.SecurityHelper;
import me.trihung.exception.BadRequestException;

/**
 * MongoDB-based OrderService implementation that maintains the same DTOs
 * and provides identical data structure to the frontend
 */
@Service
public class MongoOrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SecurityHelper securityHelper;

    @Override
    @Transactional
    public OrderDto createOrderFromReservation(Reservation reservation) {
        // MongoDB implementation - internal String IDs, external UUID DTOs
        Order order = Order.builder()
                .zone(reservation.getZone())
                .owner(reservation.getOwner())
                .quantity(reservation.getQuantity())
                .totalAmount(reservation.getZone().getPrice()
                        .multiply(java.math.BigDecimal.valueOf(reservation.getQuantity())))
                .createdAt(LocalDateTime.now())
                .build();

        Order saved = orderRepository.save(order);
        
        // MapStruct handles String to UUID conversion for DTO
        return OrderMapper.INSTANCE.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderPageResponse getOrdersPaged(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isBlank()) {
            sort = Sort.by(
                    "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC,
                    sortBy
            );
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        User user = securityHelper.getCurrentUser();
        
        // Uses custom MongoDB aggregation to return OrderDto with UUID fields
        Page<OrderDto> orderPage = orderRepository.findOrderDtosByOwner(user, pageable);

        return new OrderPageResponse(
                orderPage.getContent(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.getNumber(),
                orderPage.getSize()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy order với id: " + id));
        return OrderMapper.INSTANCE.toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueDataDto> getRevenueDataByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        // MongoDB aggregation pipeline returns same DTO structure as JPA version
        return orderRepository.findRevenueDataByDateRange(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventTypeRevenueDto> getEventTypeRevenue(LocalDateTime startDate, LocalDateTime endDate, String eventType) {
        // MongoDB aggregation maintains same analytics DTO structure
        return orderRepository.findEventTypeRevenue(startDate, endDate, eventType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopEventDto> getTopEvents(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        // MongoDB aggregation with pagination maintains same DTO structure
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.findTopEvents(startDate, endDate, pageable);
    }
}