package me.trihung.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.trihung.dto.OrderDto;
import me.trihung.dto.OrderPageResponse;
import me.trihung.dto.EventTypeRevenueDto;
import me.trihung.dto.RevenueDataDto;
import me.trihung.dto.TopEventDto;
import me.trihung.entity.Order;
import me.trihung.entity.Reservation;
import me.trihung.entity.User;
import me.trihung.exception.BadRequestException;
import me.trihung.helper.SecurityHelper;
import me.trihung.mapper.OrderMapper;
import me.trihung.repository.OrderRepository;
import me.trihung.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SecurityHelper securityHelper;

    @Override
    @Transactional
    public OrderDto createOrderFromReservation(Reservation reservation) {
        BigDecimal total = reservation.getZone().getPrice()
                .multiply(BigDecimal.valueOf(reservation.getQuantity()));

        Order order = Order.builder()
                .zone(reservation.getZone())
                .owner(reservation.getOwner())
                .quantity(reservation.getQuantity())
                .totalAmount(total)
                .createdAt(LocalDateTime.now())
                .build();

        Order saved = orderRepository.save(order);
        return OrderMapper.INSTANCE.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderPageResponse getOrdersPaged(
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isBlank()) {
            sort = Sort.by(
                    "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC,
                    sortBy
            );
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        User user = securityHelper.getCurrentUser();
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
        return orderRepository.findRevenueDataByDateRange(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventTypeRevenueDto> getEventTypeRevenue(LocalDateTime startDate, LocalDateTime endDate, String eventType) {
        return orderRepository.findEventTypeRevenue(startDate, endDate, eventType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopEventDto> getTopEvents(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.findTopEvents(startDate, endDate, pageable);
    }
}
