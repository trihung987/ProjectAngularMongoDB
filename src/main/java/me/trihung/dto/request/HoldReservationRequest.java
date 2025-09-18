package me.trihung.dto.request;

import java.util.UUID;
import lombok.Data;

@Data
public class HoldReservationRequest {
    private UUID zoneId;  
    private int quantity;  
}
