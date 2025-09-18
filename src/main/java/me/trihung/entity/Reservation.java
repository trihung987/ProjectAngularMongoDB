package me.trihung.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.trihung.enums.ReservationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "reservations")
public class Reservation {

    @Id
    private String id;

    @DBRef
    private Zone zone;
    
    @DBRef
    private User owner;

    private int quantity;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;
    
    private ReservationStatus status;
}
