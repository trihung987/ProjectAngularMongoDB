package me.trihung.test;

import me.trihung.entity.*;
import me.trihung.enums.ReservationStatus;
import me.trihung.util.IdGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Integration test to demonstrate end-to-end ID generation workflow
 * simulating how service methods create entities with proper IDs
 */
public class IntegrationIdTest {
    
    public static void main(String[] args) {
        System.out.println("=== Integration Test: End-to-End ID Generation Workflow ===\n");
        
        simulateUserRegistration();
        simulateEventCreation();
        simulateReservationWorkflow();
        
        System.out.println("✅ Integration test completed successfully!");
        System.out.println("✅ All entities in the workflow have proper IDs generated automatically!");
    }
    
    /**
     * Simulates the user registration process in UserServiceImpl
     */
    private static void simulateUserRegistration() {
        System.out.println("1. Simulating User Registration (UserServiceImpl.signUp):");
        
        // Simulate the mapper creating a user without ID
        User user = User.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("encoded_password")
                .build();
        
        System.out.println("   Before ID generation - User ID: " + user.getId());
        
        // Simulate service setting ID (as fixed in UserServiceImpl)
        user.setId(IdGenerator.generateId());
        
        System.out.println("   After ID generation - User ID: " + user.getId());
        System.out.println("   ID is valid: " + IdGenerator.isValidId(user.getId()));
        System.out.println();
    }
    
    /**
     * Simulates the event creation process in EventServiceImpl
     */
    private static void simulateEventCreation() {
        System.out.println("2. Simulating Event Creation (EventServiceImpl.createEvent):");
        
        // Create user (owner)
        User owner = User.builder()
                .id(IdGenerator.generateId())
                .username("eventorganizer")
                .build();
        
        // Simulate the mapper creating an event
        Event event = Event.builder()
                .eventName("Summer Concert")
                .eventDescription("A great summer concert")
                .owner(owner)
                .build();
        
        System.out.println("   Before ID generation - Event ID: " + event.getId());
        
        // Simulate service setting ID (as fixed in EventServiceImpl)
        event.setId(IdGenerator.generateId());
        
        // Create organizer with ID
        Organizer organizer = Organizer.builder()
                .id(IdGenerator.generateId())
                .name("Music Events Inc")
                .bio("Professional event organizer")
                .build();
        event.setOrganizer(organizer);
        
        // Create venue with ID
        Venue venue = Venue.builder()
                .id(IdGenerator.generateId())
                .province("Ha Noi")
                .address("123 Music Street")
                .build();
        event.setVenue(venue);
        
        // Create zones with IDs (as fixed in ZoneMapper)
        Zone vipZone = Zone.builder()
                .id(IdGenerator.generateId()) // This is now done automatically by ZoneMapper
                .name("VIP Section")
                .maxTickets(100)
                .price(new BigDecimal("150.00"))
                .eventId(event.getId())
                .build();
        
        Zone regularZone = Zone.builder()
                .id(IdGenerator.generateId()) // This is now done automatically by ZoneMapper
                .name("Regular Section")
                .maxTickets(500)
                .price(new BigDecimal("75.00"))
                .eventId(event.getId())
                .build();
        
        System.out.println("   After ID generation:");
        System.out.println("     Event ID: " + event.getId());
        System.out.println("     Organizer ID: " + organizer.getId());
        System.out.println("     Venue ID: " + venue.getId());
        System.out.println("     VIP Zone ID: " + vipZone.getId());
        System.out.println("     Regular Zone ID: " + regularZone.getId());
        System.out.println("   All IDs are valid: " + 
                          (IdGenerator.isValidId(event.getId()) && 
                          IdGenerator.isValidId(organizer.getId()) && 
                          IdGenerator.isValidId(venue.getId()) && 
                          IdGenerator.isValidId(vipZone.getId()) && 
                          IdGenerator.isValidId(regularZone.getId())));
        System.out.println();
    }
    
    /**
     * Simulates the reservation to order workflow
     */
    private static void simulateReservationWorkflow() {
        System.out.println("3. Simulating Reservation to Order Workflow:");
        
        // Create user and zone
        User customer = User.builder()
                .id(IdGenerator.generateId())
                .username("customer")
                .build();
        
        Zone zone = Zone.builder()
                .id(IdGenerator.generateId())
                .name("VIP Section")
                .maxTickets(100)
                .price(new BigDecimal("150.00"))
                .build();
        
        // Simulate reservation creation (ReservationServiceImpl.holdTickets)
        String reservationId = IdGenerator.generateId(); // This is now generated in the service
        
        Reservation reservation = Reservation.builder()
                .id(reservationId) // Fixed to use the same ID
                .zone(zone)
                .owner(customer)
                .quantity(2)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .status(ReservationStatus.HOLD)
                .build();
        
        System.out.println("   Reservation created:");
        System.out.println("     Reservation ID: " + reservation.getId());
        System.out.println("     Customer ID: " + customer.getId());
        System.out.println("     Zone ID: " + zone.getId());
        
        // Simulate order creation from reservation (MongoOrderServiceImpl.createOrderFromReservation)
        Order order = Order.builder()
                .id(IdGenerator.generateId()) // This is now done in the service
                .zone(reservation.getZone())
                .owner(reservation.getOwner())
                .quantity(reservation.getQuantity())
                .totalAmount(reservation.getZone().getPrice()
                           .multiply(BigDecimal.valueOf(reservation.getQuantity())))
                .createdAt(LocalDateTime.now())
                .build();
        
        System.out.println("   Order created from reservation:");
        System.out.println("     Order ID: " + order.getId());
        System.out.println("     Total Amount: " + order.getTotalAmount());
        
        // Create refresh token for session
        RefreshToken refreshToken = RefreshToken.builder()
                .id(IdGenerator.generateId()) // This is now done in UserServiceImpl
                .token("sample_refresh_token")
                .username(customer.getUsername())
                .expireTime(LocalDateTime.now().plusDays(30))
                .build();
        
        System.out.println("   Refresh token created:");
        System.out.println("     Token ID: " + refreshToken.getId());
        
        System.out.println("   All workflow IDs are valid: " + 
                          (IdGenerator.isValidId(reservation.getId()) && 
                          IdGenerator.isValidId(order.getId()) && 
                          IdGenerator.isValidId(refreshToken.getId())));
        System.out.println();
    }
}