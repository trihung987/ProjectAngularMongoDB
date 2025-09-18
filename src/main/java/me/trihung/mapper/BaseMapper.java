package me.trihung.mapper;

import org.mapstruct.Named;
import java.util.UUID;

public interface BaseMapper {
    
    @Named("stringToUuid")
    default UUID stringToUuid(String id) {
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

    @Named("uuidToString")
    default String uuidToString(UUID id) {
        return id != null ? id.toString() : null;
    }
}