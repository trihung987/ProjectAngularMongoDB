package me.trihung.entity;

import jakarta.persistence.*;
import lombok.*;
import me.trihung.enums.EventStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = true)
    private String eventName;

    private String eventImage; 
    private String eventBanner; 

    private String eventCategory;

    @Column(columnDefinition = "TEXT")
    private String eventDescription;

    @Column(nullable = true, unique = true)
    private String slug;

    private LocalDate startDate;
    private LocalDate endDate;
    
    private LocalTime startTime;
    private LocalTime endTime;
    private String timezone;
    
    @ManyToOne(fetch = FetchType.LAZY)  
    @JoinColumn(name = "owner_id", nullable = false)  
    private User owner;

    @ManyToOne(cascade = CascadeType.PERSIST) // Cascade.PERSIST: khi lưu event, nếu venue chưa có sẽ tự lưu venue
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Zone> zones = new ArrayList<>();
    
    @Enumerated(EnumType.STRING) // Lưu giá trị số của enum (0, 1, 2...)
    @Column(name = "status")
    private EventStatus status = EventStatus.DRAFT; // Mặc định là DRAFT

    // nhúng các field trong BankInfo vào trong này
    @Embedded
    private BankInfo bankInfo;
        
    public void addZone(Zone zone) {
        zones.add(zone);
        zone.setEvent(this);
    }

    public void removeZone(Zone zone) {
        zones.remove(zone);
        zone.setEvent(null);
    }
}