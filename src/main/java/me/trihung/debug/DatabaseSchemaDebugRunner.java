package me.trihung.debug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import me.trihung.entity.Order;
import lombok.extern.slf4j.Slf4j;

/**
 * Debug utility to test the database schema and data
 */
@Component
@Slf4j
public class DatabaseSchemaDebugRunner implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;
    
    // Set this to true to enable debug testing
    private static final boolean ENABLE_DEBUG = false;

    @Override
    public void run(String... args) throws Exception {
        if (!ENABLE_DEBUG) {
            log.debug("Schema debug runner disabled");
            return;
        }
        
        log.info("=== Starting Database Schema Debug ===");
        
        try {
            // Check if orders collection exists and has data
            long orderCount = mongoTemplate.count(new Query(), "orders");
            log.info("Total orders in database: {}", orderCount);
            
            if (orderCount > 0) {
                // Get a sample order to see the actual structure
                Order sampleOrder = mongoTemplate.findOne(new Query().limit(1), Order.class, "orders");
                if (sampleOrder != null) {
                    log.info("Sample order structure:");
                    log.info("  - ID: {}", sampleOrder.getId());
                    log.info("  - Quantity: {}", sampleOrder.getQuantity());
                    log.info("  - Total Amount: {}", sampleOrder.getTotalAmount());
                    log.info("  - Created At: {}", sampleOrder.getCreatedAt());
                    
                    if (sampleOrder.getOwner() != null) {
                        log.info("  - Owner ID: {}", sampleOrder.getOwner().getId());
                        log.info("  - Owner Username: {}", sampleOrder.getOwner().getUsername());
                    }
                    
                    if (sampleOrder.getZone() != null) {
                        log.info("  - Zone ID: {}", sampleOrder.getZone().getId());
                        log.info("  - Zone Name: {}", sampleOrder.getZone().getName());
                        log.info("  - Zone Price: {}", sampleOrder.getZone().getPrice());
                        log.info("  - Zone Event ID: {}", sampleOrder.getZone().getEventId());
                    }
                }
            }
            
            // Check users
            long userCount = mongoTemplate.count(new Query(), "users");
            log.info("Total users in database: {}", userCount);
            
            // Check zones
            long zoneCount = mongoTemplate.count(new Query(), "zones");
            log.info("Total zones in database: {}", zoneCount);
            
            // Check events
            long eventCount = mongoTemplate.count(new Query(), "events");
            log.info("Total events in database: {}", eventCount);
            
        } catch (Exception e) {
            log.error("Error during schema debug: {}", e.getMessage(), e);
        }
        
        log.info("=== Database Schema Debug Complete ===");
    }
}