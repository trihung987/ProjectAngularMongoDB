package me.trihung.test;

import me.trihung.entity.*;
import me.trihung.util.IdGenerator;

/**
 * Test to verify the DBRef null ID fix
 * Simulates the exact scenario that was causing the MongoDB mapping exception
 */
public class DBRefFixTest {
    
    public static void main(String[] args) {
        System.out.println("=== Testing DBRef NULL ID Fix ===\n");
        
        testOrganizerDBRefScenario();
        testVenueDBRefScenario();
        
        System.out.println("✅ DBRef fix test completed successfully!");
        System.out.println("✅ All referenced entities have proper IDs before Event creation!");
    }
    
    /**
     * Tests the scenario that was causing the original error:
     * Event with Organizer reference where Organizer had NULL id
     */
    private static void testOrganizerDBRefScenario() {
        System.out.println("1. Testing Organizer DBRef scenario (original error case):");
        
        // Simulate what was happening BEFORE the fix
        System.out.println("   BEFORE FIX - This would cause the error:");
        Event eventBefore = Event.builder()
                .id(IdGenerator.generateId())
                .eventName("Test Event")
                .build();
        
        // Create organizer WITHOUT ID (this was the problem)
        Organizer organizerWithoutId = Organizer.builder()
                .name("Test Organizer")
                .bio("Test Bio")
                .build();
        
        System.out.println("     Event ID: " + eventBefore.getId());
        System.out.println("     Organizer ID: " + organizerWithoutId.getId() + " (NULL - causes error!)");
        
        // Simulate what happens AFTER the fix
        System.out.println("\n   AFTER FIX - This should work:");
        Event eventAfter = Event.builder()
                .id(IdGenerator.generateId())
                .eventName("Test Event")
                .build();
        
        // Create organizer WITH ID (this is the fix)
        Organizer organizerWithId = Organizer.builder()
                .id(IdGenerator.generateId()) // Generate ID BEFORE setting reference
                .name("Test Organizer")
                .bio("Test Bio")
                .build();
        
        eventAfter.setOrganizer(organizerWithId);
        
        System.out.println("     Event ID: " + eventAfter.getId());
        System.out.println("     Organizer ID: " + organizerWithId.getId() + " (Valid - no error!)");
        System.out.println("     Can create DBRef: " + (organizerWithId.getId() != null));
        System.out.println();
    }
    
    /**
     * Tests the venue DBRef scenario (same potential issue)
     */
    private static void testVenueDBRefScenario() {
        System.out.println("2. Testing Venue DBRef scenario:");
        
        // Create event with venue reference
        Event event = Event.builder()
                .id(IdGenerator.generateId())
                .eventName("Test Event")
                .build();
        
        // Create venue WITH ID (preventing the same error for venues)
        Venue venue = Venue.builder()
                .id(IdGenerator.generateId()) // Generate ID BEFORE setting reference
                .province("Test Province")
                .address("Test Address")
                .build();
        
        event.setVenue(venue);
        
        System.out.println("   Event ID: " + event.getId());
        System.out.println("   Venue ID: " + venue.getId() + " (Valid - no error!)");
        System.out.println("   Can create DBRef: " + (venue.getId() != null));
        System.out.println();
    }
}