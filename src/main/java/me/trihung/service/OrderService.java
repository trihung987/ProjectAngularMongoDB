package me.trihung.service;

import java.util.UUID;

import me.trihung.dto.OrderDto;
import me.trihung.dto.OrderPageResponse;
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
}
