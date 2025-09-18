package me.trihung.service;

import me.trihung.dto.OrderDto;
import me.trihung.dto.ReservationDto;
import me.trihung.dto.request.HoldReservationRequest;
import me.trihung.entity.Reservation;

import java.util.UUID;

public interface ReservationService {

	ReservationDto holdTickets(HoldReservationRequest holdReservationRequest);

	OrderDto confirmReservation(UUID reservationId);
	
	Reservation markAsPendingPayment(UUID reservationId);

	void cancelReservation(UUID reservationId);
	
	ReservationDto getReservationById(UUID reservationId);
}
