package me.trihung.entity;

import lombok.*;
import me.trihung.enums.EventStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "events")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Event extends BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String eventName;

    private String eventImage; 
    private String eventBanner; 

    private String eventCategory;

    private String eventDescription;

    private String slug;

    private LocalDate startDate;
    private LocalDate endDate;
    
    private LocalTime startTime;
    private LocalTime endTime;
    private String timezone;
    
    @DBRef
    private User owner;

    @DBRef
    private Venue venue;

    @DBRef
    private Organizer organizer;
    
    private List<Zone> zones = new ArrayList<>();
    
    private EventStatus status = EventStatus.DRAFT;

    private BankInfo bankInfo;
        
    public void addZone(Zone zone) {
        zones.add(zone);
        zone.setEventId(this.id);
    }

    public void removeZone(Zone zone) {
        zones.remove(zone);
        zone.setEventId(null);
    }
}