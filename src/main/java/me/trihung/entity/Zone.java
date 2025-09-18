package me.trihung.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import me.trihung.enums.Shape;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "zones")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Zone extends BaseEntity {
    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String name;
    
    private Integer maxTickets; 
    
    private BigDecimal price;

    private String color;

    private Shape shape;

    private Boolean isSellable;
    
    private Boolean isSeatingZone;
    
    private String description;
    
    private Double rotation;

    private String coordinates; 

    // Reference to Event by ID instead of embedded object
    private String eventId;
    
    private Integer soldTickets = 0;
}