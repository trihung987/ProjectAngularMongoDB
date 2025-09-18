package me.trihung.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.trihung.dto.EventDto;
import me.trihung.dto.request.EventRequest;
import me.trihung.entity.Zone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ZoneMapper {

    @Autowired
    private ObjectMapper objectMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "coordinates", expression = "java(toJson(zoneRequest.getCoordinates()))")
    public abstract Zone toZone(EventRequest.ZoneRequest zoneRequest);

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