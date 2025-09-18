package me.trihung.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.trihung.dto.EventDto;
import me.trihung.dto.request.EventRequest;
import org.springframework.stereotype.Component;

@Component // Đánh dấu là một Spring Bean
public class JsonMapperHelper {

    private final ObjectMapper objectMapper;

    // Sử dụng constructor injection, đây là cách được khuyến khích
    public JsonMapperHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Method để chuyển đổi từ object CoordinatesRequest sang JSON String
    public String toJson(EventRequest.CoordinatesRequest coordinates) {
        if (coordinates == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(coordinates);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing coordinates to JSON", e);
        }
    }

    // Method để chuyển đổi từ JSON String sang object CoordinatesDto
    public EventDto.CoordinatesDto fromJson(String json) {
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