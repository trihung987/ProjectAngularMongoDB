package me.trihung.mapper;

import org.mapstruct.Named;
import java.util.UUID;

public interface BaseMapper {
    
    @Named("stringToUuid")
    default UUID stringToUuid(String id) {
        return id != null ? UUID.fromString(id) : null;
    }

    @Named("uuidToString")
    default String uuidToString(UUID id) {
        return id != null ? id.toString() : null;
    }
}