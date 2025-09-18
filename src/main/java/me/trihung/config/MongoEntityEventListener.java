package me.trihung.config;

import me.trihung.util.IdGenerator;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * MongoDB event listener that automatically generates IDs for entities before they are converted to documents.
 * This provides a fallback mechanism to ensure all entities have IDs when saved to MongoDB.
 */
@Component
public class MongoEntityEventListener extends AbstractMongoEventListener<Object> {

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object source = event.getSource();
        
        try {
            // Use reflection to find the 'id' field
            Field idField = findIdField(source.getClass());
            if (idField != null) {
                idField.setAccessible(true);
                Object currentId = idField.get(source);
                
                // If the ID is null or empty, generate a new one
                if (currentId == null || (currentId instanceof String && ((String) currentId).trim().isEmpty())) {
                    idField.set(source, IdGenerator.generateId());
                }
            }
        } catch (Exception e) {
            // Log the error but don't break the save operation
            System.err.println("Error auto-generating ID for entity: " + e.getMessage());
        }
    }
    
    /**
     * Recursively searches for an 'id' field in the class hierarchy
     */
    private Field findIdField(Class<?> clazz) {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField("id");
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}