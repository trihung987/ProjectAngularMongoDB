package me.trihung.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import me.trihung.entity.Reservation;
import me.trihung.enums.ReservationStatus;

public interface ReservationRepository extends MongoRepository<Reservation, String>, CustomReservationRepository {

    List<Reservation> findByStatusAndExpiresAtBefore(ReservationStatus status, LocalDateTime now);
    
    @Query(value = "{'expiresAt': {'$lt': ?0}}", delete = true)
    long deleteByExpiresAtBefore(LocalDateTime now);
}
