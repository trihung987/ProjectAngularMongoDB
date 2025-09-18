package me.trihung.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    @DBRef
    private Zone zone; 

    @DBRef
    private User owner;

    private Integer quantity;

    private BigDecimal totalAmount;  

    private LocalDateTime createdAt;
}
