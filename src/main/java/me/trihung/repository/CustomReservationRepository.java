package me.trihung.repository;

import java.time.LocalDateTime;

import me.trihung.entity.Reservation;
import me.trihung.entity.User;
import me.trihung.entity.Zone;
import me.trihung.enums.ReservationStatus;

public interface CustomReservationRepository {
    int countActiveReservations(String zoneId);
    boolean tryInsertReservation(String id, Zone zone, User owner, int quantity, 
                                LocalDateTime createdAt, LocalDateTime expiresAt, ReservationStatus status);
}