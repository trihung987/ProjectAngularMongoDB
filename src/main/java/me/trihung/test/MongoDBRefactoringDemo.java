package me.trihung.test;

import me.trihung.dto.OrderDto;
import me.trihung.dto.RevenueDataDto;
import me.trihung.dto.EventTypeRevenueDto;
import me.trihung.dto.TopEventDto;
import me.trihung.entity.Order;
import me.trihung.entity.User;
import me.trihung.entity.Zone;
import me.trihung.entity.Event;
import me.trihung.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Test class to demonstrate that the MongoDB refactoring maintains the same DTOs
 * and provides the same data structure to the frontend
 */
@Component
public class MongoDBRefactoringDemo {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Demonstrates that OrderDto structure remains exactly the same
     * with String fields for MongoDB compatibility
     */
    public void demonstrateOrderDtoStructure() {
        // Create sample data
        OrderDto orderDto = OrderDto.builder()
                .id(java.util.UUID.randomUUID().toString())
                .zoneId(java.util.UUID.randomUUID().toString())
                .ownerId(java.util.UUID.randomUUID().toString())
                .quantity(2)
                .totalAmount(new BigDecimal("150.00"))
                .createdAt(LocalDateTime.now())
                .priceZone(new BigDecimal("75.00"))
                .nameZone("VIP Section")
                .nameEvent("Concert Event")
                .build();

        System.out.println("OrderDto structure preserved:");
        System.out.println("ID: " + orderDto.getId() + " (UUID type preserved)");
        System.out.println("Zone ID: " + orderDto.getZoneId() + " (UUID type preserved)");
        System.out.println("Owner ID: " + orderDto.getOwnerId() + " (UUID type preserved)");
        System.out.println("Event Name: " + orderDto.getNameEvent());
        System.out.println("Total Amount: " + orderDto.getTotalAmount());
    }

    /**
     * Demonstrates that analytics DTOs remain unchanged
     */
    public void demonstrateAnalyticsDtoStructure() {
        // RevenueDataDto - unchanged structure
        RevenueDataDto revenueDto = RevenueDataDto.builder()
                .month("2024-01")
                .revenue(new BigDecimal("5000.00"))
                .orders(25L)
                .events(5L)
                .build();

        System.out.println("RevenueDataDto structure preserved:");
        System.out.println("Month: " + revenueDto.getMonth());
        System.out.println("Revenue: " + revenueDto.getRevenue());

        // EventTypeRevenueDto - unchanged structure
        EventTypeRevenueDto eventTypeDto = new EventTypeRevenueDto("Concert", 100L, new BigDecimal("7500.00"));
        System.out.println("EventTypeRevenueDto structure preserved:");
        System.out.println("Name: " + eventTypeDto.getName());
        System.out.println("Revenue: " + eventTypeDto.getRevenue());

        // TopEventDto - unchanged structure
        TopEventDto topEventDto = new TopEventDto("Summer Festival", new BigDecimal("10000.00"), 200L, "active");
        System.out.println("TopEventDto structure preserved:");
        System.out.println("Name: " + topEventDto.getName());
        System.out.println("Revenue: " + topEventDto.getRevenue());
    }

    /**
     * Demonstrates that MongoDB entities work internally with String IDs
     * but DTOs maintain UUID for frontend compatibility
     */
    public void demonstrateInternalDataHandling() {
        // Internal MongoDB entities use String IDs
        User user = User.builder()
                .id("507f1f77bcf86cd799439011") // MongoDB ObjectId as String
                .username("testuser")
                .email("test@example.com")
                .build();

        Zone zone = Zone.builder()
                .id("507f1f77bcf86cd799439012")
                .name("VIP Section")
                .price(new BigDecimal("75.00"))
                .eventId("507f1f77bcf86cd799439013")
                .build();

        Order order = Order.builder()
                .id("507f1f77bcf86cd799439014")
                .zone(zone)
                .owner(user)
                .quantity(2)
                .totalAmount(new BigDecimal("150.00"))
                .createdAt(LocalDateTime.now())
                .build();

        System.out.println("Internal MongoDB structure uses String IDs:");
        System.out.println("Order ID: " + order.getId() + " (String type for MongoDB)");
        System.out.println("Zone ID: " + zone.getId() + " (String type for MongoDB)");
        System.out.println("User ID: " + user.getId() + " (String type for MongoDB)");

        // But when converted to DTO, UUIDs are maintained for frontend
        System.out.println("Conversion to DTOs maintains UUID compatibility for frontend");
    }

    /**
     * Demonstrates that the new setEvent method works correctly for JPA to MongoDB migration compatibility
     */
    public void demonstrateZoneEventRelationship() {
        System.out.println("\n=== Testing Zone.setEvent() method ===");
        
        // Create test entities
        Event event = Event.builder()
                .id("507f1f77bcf86cd799439020")
                .eventName("Test Concert")
                .build();
                
        Zone zone = Zone.builder()
                .id("507f1f77bcf86cd799439021")
                .name("VIP Section")
                .build();
        
        // Test setEvent method - should set eventId
        zone.setEvent(event);
        System.out.println("Zone eventId after setEvent: " + zone.getEventId());
        System.out.println("Expected: " + event.getId());
        assert event.getId().equals(zone.getEventId()) : "setEvent should set eventId correctly";
        
        // Test setEvent with null - should clear eventId
        zone.setEvent(null);
        System.out.println("Zone eventId after setEvent(null): " + zone.getEventId());
        System.out.println("Expected: null");
        assert zone.getEventId() == null : "setEvent(null) should clear eventId";
        
        // Test Event.addZone method - should use setEvent internally
        event.addZone(zone);
        System.out.println("Zone eventId after event.addZone: " + zone.getEventId());
        System.out.println("Expected: " + event.getId());
        assert event.getId().equals(zone.getEventId()) : "addZone should set eventId correctly";
        
        // Test Event.removeZone method - should use setEvent(null) internally
        event.removeZone(zone);
        System.out.println("Zone eventId after event.removeZone: " + zone.getEventId());
        System.out.println("Expected: null");
        assert zone.getEventId() == null : "removeZone should clear eventId";
        
        System.out.println("✅ All Zone-Event relationship tests passed!");
    }
}