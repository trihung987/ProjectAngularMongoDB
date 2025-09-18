package me.trihung.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private UUID id;
    private UUID zoneId;
    private UUID ownerId;
    private Integer quantity;
    private BigDecimal totalAmount;   
    private LocalDateTime createdAt;
    private BigDecimal priceZone; 
    private String nameZone;
    private String nameEvent;
}
