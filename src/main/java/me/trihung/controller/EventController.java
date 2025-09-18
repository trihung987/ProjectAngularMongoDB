package me.trihung.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import lombok.SneakyThrows;
import me.trihung.dto.EventDto;
import me.trihung.dto.EventPageResponse;
import me.trihung.dto.request.EventRequest;
import me.trihung.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
 
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper; 
    
    @Operation(summary = "Tạo mới event")
    @SneakyThrows
    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<EventDto> createEvent(
            @RequestPart("eventData") String eventDataJson,
            @RequestPart(value = "eventImage", required = false) MultipartFile eventImage,
            @RequestPart(value = "eventBanner", required = false) MultipartFile eventBanner,
            @RequestPart(value = "organizerLogo", required = false) MultipartFile organizerLogo
    ) {
        EventRequest requestDto = objectMapper.readValue(eventDataJson, EventRequest.class);

        requestDto.setEventImage(eventImage);
        requestDto.setEventBanner(eventBanner);
        if (requestDto.getOrganizer() != null) {
            requestDto.getOrganizer().setLogo(organizerLogo);
        }

        EventDto createdEvent = eventService.createEvent(requestDto);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Lưu nháp event")
    @SneakyThrows
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/draft", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<EventDto> saveDraft(
            @RequestPart("eventData") String eventDataJson,
            @RequestPart(value = "eventImage", required = false) MultipartFile eventImage,
            @RequestPart(value = "eventBanner", required = false) MultipartFile eventBanner,
            @RequestPart(value = "organizerLogo", required = false) MultipartFile organizerLogo) {
        
        EventRequest requestDto = objectMapper.readValue(eventDataJson, EventRequest.class);

        requestDto.setEventImage(eventImage);
        requestDto.setEventBanner(eventBanner);
        if (requestDto.getOrganizer() != null) {
            requestDto.getOrganizer().setLogo(organizerLogo);
        }

        EventDto savedDraft = eventService.saveDraft(requestDto);
        return new ResponseEntity<>(savedDraft, HttpStatus.OK);
    }
    
    @Operation(summary = "Trả về dánh sách event theo user đã authen")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/page")
    public ResponseEntity<EventPageResponse> getEventsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        EventPageResponse response = eventService.getEventsPaged(page, size, status, search, sortBy, sortDirection, true);
        return ResponseEntity.status(200).body(response);
    }
    
    @Operation(summary = "Trả về dánh sách event đã published")
    @GetMapping("/pagepublic")
    public ResponseEntity<EventPageResponse> getEventsPagedPublic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        EventPageResponse response = eventService.getEventsPaged(page, size, "PUBLISHED", search, sortBy, sortDirection, false);
        return ResponseEntity.ok(response);
    }
    
//    @GetMapping
//    public ResponseEntity<List<EventDto>> getAllEvents() {
//        List<EventDto> events = eventService.getAllEvents();
//        return ResponseEntity.ok(events);
//    }
    
    @Operation(summary = "Lấy thông tin chi tiết về event")
    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable UUID id) {
        EventDto event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }
    
    @Operation(summary = "Xóa event")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok().build();
    }
}