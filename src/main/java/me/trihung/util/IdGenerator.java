package me.trihung.util;

import java.util.UUID;

/**
 * Utility class for consistent ID generation across MongoDB entities
 */
public class IdGenerator {
    
    /**
     * Generates a UUID string for MongoDB entities
     * @return A UUID string suitable for MongoDB document IDs
     */
    public static String generateId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Validates if an ID is properly formatted
     * @param id The ID to validate
     * @return true if the ID is valid, false otherwise
     */
    public static boolean isValidId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            // Could be a MongoDB ObjectId or other valid format
            return id.length() >= 12; // Basic length check for ObjectId
        }
    }
}