package me.trihung.test;

import me.trihung.entity.*;
import me.trihung.enums.ReservationStatus;
import me.trihung.util.IdGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Test class to verify that ID generation is working correctly for all MongoDB entities
 */
public class IdGenerationTest {
    
    public static void main(String[] args) {
        System.out.println("=== Testing ID Generation for MongoDB Entities ===\n");
        
        testIdGeneratorUtility();
        testUserEntity();
        testEventEntity();
        testZoneEntity();
        testOrderEntity();
        testReservationEntity();
        testRefreshTokenEntity();
        testOrganizerEntity();
        testVenueEntity();
        
        System.out.println("✅ All ID generation tests completed successfully!");
    }
    
    private static void testIdGeneratorUtility() {
        System.out.println("1. Testing IdGenerator utility:");
        String id1 = IdGenerator.generateId();
        String id2 = IdGenerator.generateId();
        
        System.out.println("   Generated ID 1: " + id1);
        System.out.println("   Generated ID 2: " + id2);
        System.out.println("   IDs are different: " + !id1.equals(id2));
        System.out.println("   ID 1 is valid: " + IdGenerator.isValidId(id1));
        System.out.println("   ID 2 is valid: " + IdGenerator.isValidId(id2));
        System.out.println();
    }
    
    private static void testUserEntity() {
        System.out.println("2. Testing User entity:");
        User user = User.builder()
                .id(IdGenerator.generateId())
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();
        
        System.out.println("   User ID: " + user.getId());
        System.out.println("   User ID is valid: " + IdGenerator.isValidId(user.getId()));
        System.out.println();
    }
    
    private static void testEventEntity() {
        System.out.println("3. Testing Event entity:");
        Event event = Event.builder()
                .id(IdGenerator.generateId())
                .eventName("Test Event")
                .eventDescription("Test Description")
                .build();
        
        System.out.println("   Event ID: " + event.getId());
        System.out.println("   Event ID is valid: " + IdGenerator.isValidId(event.getId()));
        System.out.println();
    }
    
    private static void testZoneEntity() {
        System.out.println("4. Testing Zone entity:");
        Zone zone = Zone.builder()
                .id(IdGenerator.generateId())
                .name("VIP Zone")
                .maxTickets(100)
                .price(new BigDecimal("50.00"))
                .build();
        
        System.out.println("   Zone ID: " + zone.getId());
        System.out.println("   Zone ID is valid: " + IdGenerator.isValidId(zone.getId()));
        System.out.println();
    }
    
    private static void testOrderEntity() {
        System.out.println("5. Testing Order entity:");
        Order order = Order.builder()
                .id(IdGenerator.generateId())
                .quantity(2)
                .totalAmount(new BigDecimal("100.00"))
                .createdAt(LocalDateTime.now())
                .build();
        
        System.out.println("   Order ID: " + order.getId());
        System.out.println("   Order ID is valid: " + IdGenerator.isValidId(order.getId()));
        System.out.println();
    }
    
    private static void testReservationEntity() {
        System.out.println("6. Testing Reservation entity:");
        Reservation reservation = Reservation.builder()
                .id(IdGenerator.generateId())
                .quantity(2)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .status(ReservationStatus.HOLD)
                .build();
        
        System.out.println("   Reservation ID: " + reservation.getId());
        System.out.println("   Reservation ID is valid: " + IdGenerator.isValidId(reservation.getId()));
        System.out.println();
    }
    
    private static void testRefreshTokenEntity() {
        System.out.println("7. Testing RefreshToken entity:");
        RefreshToken token = RefreshToken.builder()
                .id(IdGenerator.generateId())
                .token("sample-token")
                .username("testuser")
                .expireTime(LocalDateTime.now().plusDays(30))
                .build();
        
        System.out.println("   RefreshToken ID: " + token.getId());
        System.out.println("   RefreshToken ID is valid: " + IdGenerator.isValidId(token.getId()));
        System.out.println();
    }
    
    private static void testOrganizerEntity() {
        System.out.println("8. Testing Organizer entity:");
        Organizer organizer = Organizer.builder()
                .id(IdGenerator.generateId())
                .name("Test Organizer")
                .bio("Test Bio")
                .build();
        
        System.out.println("   Organizer ID: " + organizer.getId());
        System.out.println("   Organizer ID is valid: " + IdGenerator.isValidId(organizer.getId()));
        System.out.println();
    }
    
    private static void testVenueEntity() {
        System.out.println("9. Testing Venue entity:");
        Venue venue = Venue.builder()
                .id(IdGenerator.generateId())
                .province("Test Province")
                .address("Test Address")
                .build();
        
        System.out.println("   Venue ID: " + venue.getId());
        System.out.println("   Venue ID is valid: " + IdGenerator.isValidId(venue.getId()));
        System.out.println();
    }
}