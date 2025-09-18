package me.trihung.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import me.trihung.enums.EventStatus;
import me.trihung.enums.Shape;


@Data
public class EventDto {
    private UUID id;
    private String eventName;
    private String eventImage; 
    private String eventBanner; 
    private String eventCategory;
    private String eventDescription;
    private VenueDto venue;
    private OrganizerDto organizer;
    private LocalDate startDate;
    private LocalDate endDate;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private LocalDateTime updatedDate;
    private String slug;
    private String timezone;
    private EventStatus eventStatus;
    private List<ZoneDto> zones;
    private BankInfoDto bankInfo;
    private UUID ownerId;
    
    private int totalSeats;
    private BigDecimal totalRevenue;

    @Data
    public static class VenueDto {
        private String province;
        private String address;
    }

    @Data
    public static class OrganizerDto {
        private String name;
        private String bio;
        private String logo; 
    }

    @Data
    public static class PointDto {
        private double x;
        private double y;
    }

    @Data
    public static class CoordinatesDto {
        private double x;
        private double y;
        private Double width;
        private Double height;
        private Double radius;
        private List<PointDto> points;
    }

    @Data
    public static class ZoneDto {
        private UUID id;
        private String name;
        private Integer maxTickets;
        private BigDecimal price;
        
        private String color;
        private Shape shape;
        private Boolean isSellable;
        private Boolean isSeatingZone;
        private String description;
        private Double rotation;
        private CoordinatesDto coordinates;
        private Integer soldTickets;
    }

    @Data
    public static class BankInfoDto {
        private String accountHolder;
        private String accountNumber;
        private String bankName;
        private String bankBranch;
    }
}