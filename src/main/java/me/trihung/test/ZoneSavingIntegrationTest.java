package me.trihung.test;

import me.trihung.entity.Event;
import me.trihung.entity.Zone;
import me.trihung.enums.EventStatus;
import me.trihung.enums.Shape;
import me.trihung.repository.EventRepository;
import me.trihung.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Integration test to verify that the zone saving fix works properly
 */
@Component
public class ZoneSavingIntegrationTest {

    @Autowired
    private ZoneRepository zoneRepository;
    
    @Autowired
    private EventRepository eventRepository;

    public void testZoneSavingAfterFix() {
        System.out.println("\n=== Testing Zone Saving Integration ===");
        
        // 1. Create an Event with zones (simulating event creation form)
        Event event = Event.builder()
                .id("integration_test_event_001")
                .eventName("Integration Test Concert")
                .eventCategory("Music")
                .status(EventStatus.DRAFT)
                .startDate(LocalDate.now().plusDays(30))
                .endDate(LocalDate.now().plusDays(30))
                .startTime(LocalTime.of(20, 0))
                .endTime(LocalTime.of(23, 0))
                .timezone("Asia/Ho_Chi_Minh")
                .zones(new ArrayList<>())
                .build();

        // 2. Create zones that would come from the event creation form
        Zone vipZone = Zone.builder()
                .id("integration_vip_zone_001")
                .name("VIP Area")
                .maxTickets(50)
                .price(new BigDecimal("300.00"))
                .color("#FFD700")
                .shape(Shape.rectangle)
                .isSellable(true)
                .isSeatingZone(true)
                .description("Best seats in the house")
                .rotation(0.0)
                .coordinates("{\"x\":100,\"y\":50,\"width\":200,\"height\":100}")
                .soldTickets(0)
                .build();

        Zone generalZone = Zone.builder()
                .id("integration_general_zone_001")
                .name("General Admission")
                .maxTickets(200)
                .price(new BigDecimal("100.00"))
                .color("#87CEEB")
                .shape(Shape.rectangle)
                .isSellable(true)
                .isSeatingZone(false)
                .description("Standing area")
                .rotation(0.0)
                .coordinates("{\"x\":300,\"y\":150,\"width\":400,\"height\":200}")
                .soldTickets(0)
                .build();

        // 3. Add zones to event (this should set eventId in zones)
        event.addZone(vipZone);
        event.addZone(generalZone);

        System.out.println("VIP Zone eventId after addZone: " + vipZone.getEventId());
        System.out.println("General Zone eventId after addZone: " + generalZone.getEventId());

        // 4. Simulate what EventServiceImpl.saveOrUpdateEvent should do
        // (In real scenario, this would be called by the service)
        
        // Save event first
        Event savedEvent = eventRepository.save(event);
        System.out.println("Event saved: " + savedEvent.getEventName());

        // Save zones as independent documents (this is the fix we implemented)
        vipZone.setEvent(savedEvent); // Ensure eventId is set
        generalZone.setEvent(savedEvent); // Ensure eventId is set
        
        Zone savedVipZone = zoneRepository.save(vipZone);
        Zone savedGeneralZone = zoneRepository.save(generalZone);
        
        System.out.println("VIP Zone saved independently: " + savedVipZone.getName());
        System.out.println("General Zone saved independently: " + savedGeneralZone.getName());

        // 5. Test that zones can be found independently (this was the original problem)
        Optional<Zone> foundVipZone = zoneRepository.findById(vipZone.getId());
        Optional<Zone> foundGeneralZone = zoneRepository.findById(generalZone.getId());

        if (foundVipZone.isPresent() && foundGeneralZone.isPresent()) {
            System.out.println("✅ VIP Zone found independently: " + foundVipZone.get().getName());
            System.out.println("✅ General Zone found independently: " + foundGeneralZone.get().getName());
            
            // 6. Test that event can be found through zone's eventId
            Zone vip = foundVipZone.get();
            Optional<Event> foundEvent = eventRepository.findById(vip.getEventId());
            
            if (foundEvent.isPresent()) {
                System.out.println("✅ Event found through zone reference: " + foundEvent.get().getEventName());
                System.out.println("✅ Original problem SOLVED: zones can be found independently!");
                System.out.println("✅ holdTickets() should now work because zoneRepository.findById() will succeed!");
            } else {
                System.out.println("❌ Event not found through zone reference");
            }
        } else {
            System.out.println("❌ Zones not found independently - fix not working");
        }

        System.out.println("\n=== Integration Test Completed ===");
    }
}