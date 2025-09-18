package me.trihung.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;
import me.trihung.enums.ReservationStatus;

@Data
public class ReservationDto {
    private UUID id;
    private UUID zoneId;
    private String nameZone;
    private String nameEvent;
    private BigDecimal priceZone;
    private UUID ownerId;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private ReservationStatus status;
    
}
