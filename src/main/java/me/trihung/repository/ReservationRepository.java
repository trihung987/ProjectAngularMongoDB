package me.trihung.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import me.trihung.entity.Reservation;
import me.trihung.enums.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    // Đếm số vé đang giữ (chưa hết hạn)
    @Query("SELECT COALESCE(SUM(r.quantity), 0) FROM Reservation r " +
           "WHERE r.zone.id = :zoneId AND r.expiresAt > CURRENT_TIMESTAMP")
    int countActiveReservations(UUID zoneId);
    
    //tạo giữ chỗ nếu còn đủ vé trong zone
    @Modifying
    @Query(value = """
        INSERT INTO reservations (id, zone_id, owner_id, quantity, created_at, expires_at, status)
        SELECT :id, :zoneId, :ownerId, :quantity, :createdAt, :expiresAt, :status
        FROM zones z
        WHERE z.id = :zoneId
          AND (z.max_tickets - z.sold_tickets -
               (SELECT COALESCE(SUM(r.quantity),0)
                FROM reservations r
                WHERE r.zone_id = z.id
                  AND r.expires_at > NOW()
                  AND r.status <> 'CANCELED')) >= :quantity
        """, nativeQuery = true)
    int tryInsertReservation(
            @Param("id") UUID id,
            @Param("zoneId") UUID zoneId,
            @Param("ownerId") UUID ownerId,
            @Param("quantity") int quantity,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("expiresAt") LocalDateTime expiresAt,
            @Param("status") String status
    );



    List<Reservation> findByStatusAndExpiresAtBefore(ReservationStatus status, LocalDateTime now);
    
    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.expiresAt < :now")
    int deleteExpiredReservations( @Param("now") LocalDateTime now);

}
