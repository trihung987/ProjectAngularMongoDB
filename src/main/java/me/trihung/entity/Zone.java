package me.trihung.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import me.trihung.enums.Shape;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "zones")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Zone extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    private String name;
    
    private Integer maxTickets; 
    
    private BigDecimal price;


    private String color;

    @Enumerated(EnumType.STRING) // Lưu tên của enum
    private Shape shape;

    private Boolean isSellable;
    
    private Boolean isSeatingZone;
    
    private String description;
    
    private Double rotation;

    @Lob // Dùng @Lob hoặc @Column(columnDefinition = "TEXT") để lưu chuỗi JSON dài
    private String coordinates; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @JsonBackReference //Chống bị lặp vô hạn event -> zone ->event ->zone...
    private Event event;
    
    @Column(nullable = false)
    private Integer soldTickets = 0;
}