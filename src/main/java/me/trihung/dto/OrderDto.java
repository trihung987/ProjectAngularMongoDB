package me.trihung.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private String id;
    private String zoneId;
    private String ownerId;
    private Integer quantity;
    private BigDecimal totalAmount;   
    private LocalDateTime createdAt;
    private BigDecimal priceZone; 
    private String nameZone;
    private String nameEvent;
}
