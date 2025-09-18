package me.trihung.dto.request;

import lombok.Data;

@Data
public class HoldReservationRequest {
    private String zoneId;  
    private int quantity;  
}
