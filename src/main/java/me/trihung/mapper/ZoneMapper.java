package me.trihung.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.trihung.dto.EventDto;
import me.trihung.dto.request.EventRequest;
import me.trihung.entity.Zone;
import me.trihung.util.IdGenerator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class ZoneMapper {

    @Autowired
    private ObjectMapper objectMapper;

    // Helper methods for ID conversion
    protected UUID stringToUuid(String id) {
        if (id == null) {
            return null;
        }
        
        // Handle MongoDB ObjectId format (24 hex chars without hyphens)
        if (id.length() == 24 && id.matches("[0-9a-fA-F]{24}")) {
            // Convert MongoDB ObjectId to UUID format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
            // Pad to 32 chars by adding zeros, then format as UUID
            String paddedId = id + "00000000"; // 24 + 8 = 32 chars
            String uuidString = paddedId.substring(0, 8) + "-" + 
                              paddedId.substring(8, 12) + "-" + 
                              paddedId.substring(12, 16) + "-" + 
                              paddedId.substring(16, 20) + "-" + 
                              paddedId.substring(20, 32);
            return UUID.fromString(uuidString);
        }
        
        // Handle standard UUID format
        return UUID.fromString(id);
    }

    protected String uuidToString(UUID id) {
        return id != null ? id.toString() : null;
    }

    @Mapping(target = "id", expression = "java(generateZoneId())")
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "coordinates", expression = "java(toJson(zoneRequest.getCoordinates()))")
    public abstract Zone toZone(EventRequest.ZoneRequest zoneRequest);
    
    // Helper method to generate ID for zones
    protected String generateZoneId() {
        return IdGenerator.generateId();
    }

    @Mapping(target = "id", expression = "java(stringToUuid(zone.getId()))")
    @Mapping(target = "coordinates", expression = "java(fromJson(zone.getCoordinates()))")
    public abstract EventDto.ZoneDto toZoneDto(Zone zone);

   //Chuyển đổi ds tọa độ từ các object sang json để dễ lưu data vào db hơn
    String toJson(EventRequest.CoordinatesRequest coordinates) {
        if (coordinates == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(coordinates);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing coordinates to JSON", e);
        }
    }

   //ngược lại
    EventDto.CoordinatesDto fromJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, EventDto.CoordinatesDto.class);
        } catch (JsonProcessingException e) {
        	throw new RuntimeException("Error deserializing coordinates from JSON", e);
        }
    }
}