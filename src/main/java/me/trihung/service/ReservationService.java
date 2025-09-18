package me.trihung.service;

import me.trihung.dto.OrderDto;
import me.trihung.dto.ReservationDto;
import me.trihung.dto.request.HoldReservationRequest;
import me.trihung.entity.Reservation;

public interface ReservationService {

	ReservationDto holdTickets(HoldReservationRequest holdReservationRequest);

	OrderDto confirmReservation(String reservationId);
	
	Reservation markAsPendingPayment(String reservationId);

	void cancelReservation(String reservationId);
	
	ReservationDto getReservationById(String reservationId);
}
